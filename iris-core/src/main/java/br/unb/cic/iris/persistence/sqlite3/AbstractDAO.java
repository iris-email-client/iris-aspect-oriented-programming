package br.unb.cic.iris.persistence.sqlite3;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;

import br.unb.cic.iris.core.exception.DBException;
import br.unb.cic.iris.util.HibernateUtil;

public abstract class AbstractDAO<T> {
	private Class<T> clazz;
	protected Session session;
	
	public AbstractDAO(){
		clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}
	
	/**
	 * Returns all the methods of a given class, including inherited ones.
	 * 
	 * @param methods
	 * @param type
	 * @return
	 */
	private static List<Method> getAllMethods(List<Method> methods, Class<?> type) {
	    methods.addAll(Arrays.asList(type.getDeclaredMethods()));

	    if (type.getSuperclass() != null)
	        methods = getAllMethods(methods, type.getSuperclass());

	    return methods;
	}
	
	public void saveOrUpdate(T obj) throws DBException {
        try {
        	List<Method> methods = getAllMethods(new ArrayList<Method>(), obj.getClass());
        	
        	Method getId = null;
        	Method setId = null;
        	
        	for (Method m : methods) {
        		if (m.getName().equals("getId"))
        			getId = m;
        		else if (m.getName().equals("setId"))
        			setId = m;
        		
        		if (getId != null && setId != null)
        			break;
        	}
        	
        	if (getId == null || setId == null)
        		throw new Exception();

        	
        	String id = (String) getId.invoke(obj, (Object []) null);
        	
        	if (id == null) // Create
        		setId.invoke(obj, UUID.randomUUID().toString());
        	// else... It should be treated as an update operation!
        	
            startSession(true);
            session.saveOrUpdate(obj);
            session.getTransaction().commit();
        } catch (HibernateException e) {
        	handleException(e);
        } catch (Exception e) {
        	throw new DBException("Couldn't call `getId()` or `setId()` methods on entity.", e);
        } finally {
        	closeSession();
        }
    }
	
	public void delete(T t) throws DBException {
		try {
            startSession(true);
            session.delete(t);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            handleException(e);
        } finally {
        	closeSession();
        }
	}
	
	public T findById(String id) throws DBException {
        T obj = null;
        try {
            startSession(false);
            obj = (T) session.load(clazz, id); //TODO: we have to discuss this. Hibernate is really annoying. 
            obj.toString();
            //session.getTransaction().commit();
        } catch (HibernateException e) {
            handleException(e);
        } finally {
        	closeSession();
        }
        return obj;
    }

    public List<T> findAll() throws DBException {
        List<T> objects = null;
        try {
            startSession(false);
            Query query = session.createQuery("from " + clazz.getName());
            objects = query.list();
            //session.getTransaction().commit();
        } catch (HibernateException e) {
            handleException(e);
        } finally {
        	closeSession();
        }
        return objects;
    }

	public List<T> findByExample(T filtro, MatchMode matchMode, boolean ignoreCase){
		org.hibernate.criterion.Example example = org.hibernate.criterion.Example.create(filtro);

		if(matchMode != null){
			example = example.enableLike(matchMode);
		}

		if(ignoreCase){
			example = example.ignoreCase();
		}

		return session.createCriteria(clazz).add(example).list();
	}
	
	protected void handleException(Exception e) throws DBException {
		if(session.getTransaction().isActive()) {
			session.getTransaction().rollback();
		}
        throw new DBException(e.getMessage(), e);
    }

    protected void startSession(boolean beginTransaction) throws HibernateException {
    	if (session == null || !session.isOpen()) {
    		session = HibernateUtil.getSessionFactory().openSession();
    	}
    	if (!session.getTransaction().isActive() && beginTransaction) {
            session.beginTransaction();
    	}
    }
    
	protected void closeSession() {
		if(session != null && session.isOpen()) {
			session.flush();
			session.close();
		}
	}
}
