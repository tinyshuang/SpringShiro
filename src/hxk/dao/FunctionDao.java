package hxk.dao;

import java.util.List;

import hxk.model.Function;


/**
 * 
 * @author hxk
 * @description dao层直接声明接口,在mapper实现
 *2015年8月24日  下午3:00:10
 */
public interface FunctionDao {
	public void save(Function function);
	public void update(Function function);
	public Function find(String userid);
	public List<Function> findAll(String userid);
	public List<Function> findByName(String userid);
	public void delete(String id);
}
