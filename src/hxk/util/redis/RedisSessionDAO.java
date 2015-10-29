package hxk.util.redis;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.ValidatingSession;
import org.apache.shiro.session.mgt.eis.CachingSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author hxk
 * @description 继承shiro提供的CachingSessionDAO,来实现aop化的CRUD缓存
 *2015年10月29日  下午2:48:55
 */
public class RedisSessionDAO extends CachingSessionDAO {
    private static Logger logger = LoggerFactory.getLogger(RedisSessionDAO.class);

    // 登录成功的信息存储在 session 的这个 attribute 里.
    private static final String AUTHENTICATED_SESSION_KEY =
            "org.apache.shiro.subject.support.DefaultSubjectContext_AUTHENTICATED_SESSION_KEY";

    private String keyPrefix = "shiro_redis_session:";
    private String deleteChannel = "shiro_redis_session:delete";
    private int timeToLiveSeconds = 1800; // Expiration of Jedis's key, unit: second

    private RedisManager redisManager;

    /**
     * DefaultSessionManager 创建完 session 后会调用该方法。
     * 把 session 保持到 Redis。
     * 返回 Session ID；主要此处返回的 ID.equals(session.getId())
     */
    @Override
    protected Serializable doCreate(Session session) {
        logger.debug("=> Create session with ID [{}]", session.getId());

        // 创建一个Id并设置给Session
        Serializable sessionId = this.generateSessionId(session);
        assignSessionId(session, sessionId);

        // session 由 Redis 缓存失效决定
        String key = SerializationUtils.sessionKey(keyPrefix, session);
        String value = SerializationUtils.sessionToString(session);
        redisManager.setex(key, value, timeToLiveSeconds);

        return sessionId;
    }

    /**
     * 决定从本地 Cache 还是从 Redis 读取 Session.
     * @param sessionId
     * @return
     * @throws UnknownSessionException
     */
    @Override
    public Session readSession(Serializable sessionId) throws UnknownSessionException {
        Session s = getCachedSession(sessionId);

        // 1. 如果本地缓存没有，则从 Redis 读取。
        // 2. ServerA 登录了，ServerB 没有登录但缓存里有此 session，所以从 Redis 读取而不是直接用缓存里的
        if (s == null || (
                s.getAttribute(AUTHENTICATED_SESSION_KEY) != null
                && !(Boolean) s.getAttribute(AUTHENTICATED_SESSION_KEY)
        )) {
            s = doReadSession(sessionId);
            if (s == null) {
                throw new UnknownSessionException("There is no session with id [" + sessionId + "]");
            }
            return s;
        }

        return s;
    }

    /**
     * 从 Redis 上读取 session，并缓存到本地 Cache.
     * @param sessionId
     * @return
     */
    @Override
    protected Session doReadSession(Serializable sessionId) {
        logger.debug("=> Read session with ID [{}]", sessionId);

        String value = redisManager.get(SerializationUtils.sessionKey(keyPrefix, sessionId));

        // 例如 Redis 调用 flushdb 情况了所有的数据，读到的 session 就是空的
        if (value != null) {
            Session session = SerializationUtils.sessionFromString(value);
            super.cache(session, session.getId());

            return session;
        }

        return null;
    }

    /**
     * 更新 session 到 Redis.
     * @param session
     */
    @Override
    protected void doUpdate(Session session) {
        // 如果会话过期/停止，没必要再更新了
        if (session instanceof ValidatingSession && !((ValidatingSession) session).isValid()) {
            logger.debug("=> Invalid session.");
            return;
        }

        logger.debug("=> Update session with ID [{}]", session.getId());

        String key = SerializationUtils.sessionKey(keyPrefix, session);
        String value = SerializationUtils.sessionToString(session);
        redisManager.setex(key, value, timeToLiveSeconds);
    }

    /**
     * 从 Redis 删除 session，并且发布消息通知其它 Server 上的 Cache 删除 session.
     * @param session
     */
    @Override
    protected void doDelete(Session session) {
        logger.debug("=> Delete session with ID [{}]", session.getId());

        redisManager.del(SerializationUtils.sessionKey(keyPrefix, session));
        // 发布消息通知其它 Server 上的 cache 删除 session.
        redisManager.publish(deleteChannel, SerializationUtils.sessionIdToString(session));

        // 放在其它类里用一个 daemon 线程执行，删除 cache 中的 session
        // jedis.subscribe(new JedisPubSub() {
        //     @Override
        //     public void onMessage(String channel, String message) {
        //         // 1. deserialize message to sessionId
        //         // 2. Session session = getCachedSession(sessionId);
        //         // 3. uncache(session);
        //     }
        // }, deleteChannel);
    }

    /**
     * 取得所有有效的 session.
     * @return
     */
    @Override
    public Collection<Session> getActiveSessions() {
        logger.debug("=> Get active sessions");
        Set<String> keys = redisManager.keys(keyPrefix + "*");
        Collection<String> values = redisManager.mget(keys.toArray(new String[0]));
        List<Session> sessions = new LinkedList<Session>();

        for (String value : values) {
            sessions.add(SerializationUtils.sessionFromString(value));
        }

        return sessions;
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public String getDeleteChannel() {
        return deleteChannel;
    }

    public void setDeleteChannel(String deleteChannel) {
        this.deleteChannel = deleteChannel;
    }

    public RedisManager getRedisManager() {
        return redisManager;
    }

    public void setRedisManager(RedisManager redisManager) {
        this.redisManager = redisManager;
    }

    public int getTimeToLiveSeconds() {
        return timeToLiveSeconds;
    }

    public void setTimeToLiveSeconds(int timeToLiveSeconds) {
        this.timeToLiveSeconds = timeToLiveSeconds;
    }
}
