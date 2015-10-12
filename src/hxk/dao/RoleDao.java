package hxk.dao;

import java.util.List;
import hxk.model.Role;


/**
 * 
 * @author hxk
 * @description dao层直接声明接口,在mapper实现
 *2015年8月24日  下午3:00:10
 */
public interface RoleDao {
	public void save(Role role);
	public void update(Role role);
	public Role find(String userid);
	public List<Role> findAll(String userid);
	public List<Role> findByName(String userid);
	public void delete(String id);
}
