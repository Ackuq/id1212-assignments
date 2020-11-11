package com.kth.assignment3.integrations;

import com.kth.assignment3.models.UserBean;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class UserDAO {
    
    private EntityManager entityManager;
        
    public boolean userExists(String email) {
        try {
            UserBean user = entityManager.createNamedQuery("findUser", UserBean.class)
                    .setParameter("email", email)
                    .getSingleResult();
            
            return user != null;
        } catch (Exception _e) {
            return false;
        }
    }
    
//    public int registerUser(UserBean user) throws ClassNotFoundException {
//        
//    }
}
