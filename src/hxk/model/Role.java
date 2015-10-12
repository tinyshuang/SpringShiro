package hxk.model;

import java.io.Serializable;

/**
 * @author hxk
 * @description
 *2015年10月12日  下午2:35:11
 */
public class Role implements Serializable{
    private static final long serialVersionUID = 6219549870810185457L;
    
    private String id;
    private String userid;
    private String roleName;
    
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getUserid() {
        return userid;
    }
    public void setUserid(String userid) {
        this.userid = userid;
    }
    public String getRoleName() {
        return roleName;
    }
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
    
    
    
    
}
