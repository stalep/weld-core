package org.jboss.webbeans.ejb;

import static org.jboss.webbeans.ejb.EJB.MESSAGE_DRIVEN_ANNOTATION;
import static org.jboss.webbeans.ejb.EJB.SINGLETON_ANNOTATION;
import static org.jboss.webbeans.ejb.EJB.STATEFUL_ANNOTATION;
import static org.jboss.webbeans.ejb.EJB.REMOVE_ANNOTATION;
import static org.jboss.webbeans.ejb.EJB.STATELESS_ANNOTATION;
import static org.jboss.webbeans.ejb.EjbMetaData.EjbType.MESSAGE_DRIVEN;
import static org.jboss.webbeans.ejb.EjbMetaData.EjbType.SINGLETON;
import static org.jboss.webbeans.ejb.EjbMetaData.EjbType.STATEFUL;
import static org.jboss.webbeans.ejb.EjbMetaData.EjbType.STATELESS;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.jboss.webbeans.util.Reflections;

public class EjbMetaData<T>
{

   public enum EjbType
   {
      STATELESS,
      STATEFUL,
      SINGLETON,
      MESSAGE_DRIVEN;
   }
   
   private EjbType ejbType;
   private List<Method> removeMethods;

   public EjbMetaData(Class<T> type)
   {
      // TODO Merge in ejb-jar.xml
      if (type.isAnnotationPresent(STATELESS_ANNOTATION))
      {
         this.ejbType = STATELESS;
      }
      else if (type.isAnnotationPresent(STATEFUL_ANNOTATION))
      {
         this.ejbType = STATEFUL;
         this.removeMethods = new ArrayList<Method>();
         for (Method removeMethod : Reflections.getMethods(type, REMOVE_ANNOTATION))
         {
            removeMethods.add(removeMethod);
         }
      }
      else if (type.isAnnotationPresent(MESSAGE_DRIVEN_ANNOTATION))
      {
         this.ejbType = MESSAGE_DRIVEN;
      }
      else if (type.isAnnotationPresent(SINGLETON_ANNOTATION))
      {
         this.ejbType = SINGLETON;
      }
   }

   public boolean isStateless()
   {
      return STATELESS.equals(ejbType);
   }

   public boolean isStateful()
   {
      return STATEFUL.equals(ejbType);
   }

   public boolean isMessageDriven()
   {
      return MESSAGE_DRIVEN.equals(ejbType);
   }

   public boolean isSingleton()
   {
      return SINGLETON.equals(ejbType);
   }
   
   public List<Method> getRemoveMethods()
   {
      return removeMethods;
   }

}
