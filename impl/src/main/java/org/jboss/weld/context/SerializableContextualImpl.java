/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.weld.context;

import java.io.Serializable;

import javax.enterprise.context.spi.Contextual;

import org.jboss.weld.Container;
import org.jboss.weld.serialization.spi.ContextualStore;
import org.jboss.weld.serialization.spi.helpers.SerializableContextual;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;

/**
 * A serializable version of contextual that knows how to restore the
 * original bean if necessary
 * 
 * @author pmuir
 * 
 */
public class SerializableContextualImpl<C extends Contextual<I>, I> extends ForwardingContextual<I> implements SerializableContextual<C, I>
{

   @Override
   protected Contextual<I> delegate()
   {
      return get();
   }

   private static final long serialVersionUID = 9161034819867283482L;

   // A directly serializable contextual
   private C serialiazable;
   @SuppressWarnings(value="SE_TRANSIENT_FIELD_NOT_RESTORED", justification="A cache which is lazily loaded")
   // A cached, transient version of the contextual
   private transient C cached;
   
   // the id of a non-serializable, passivation capable contextual
   private String id;
   
   private static final ContextualStore CACHED_CONTEXTUAL_STORE = Container.instance().services().get(ContextualStore.class);
   
   public SerializableContextualImpl(C contextual)
   {
      if (contextual instanceof Serializable)
      {
         // the contextual is serializable, so we can just use it
         this.serialiazable = contextual;
      }
      else
      {
         // otherwise, generate an id (may not be portable between container instances)
         this.id = CACHED_CONTEXTUAL_STORE.putIfAbsent(contextual);
      }
      // cache the contextual
      this.cached = contextual;
   }
   
   public C get()
   {
      if (cached == null)
      {
         loadContextual();
      }
      return cached;
   }
   
   private void loadContextual()
   {
      if (serialiazable != null)
      {
         this.cached = serialiazable;
      }
      else if (id != null)
      {
         this.cached = CACHED_CONTEXTUAL_STORE.<C, I>getContextual(id);
      }
   }
   
   @Override
   public boolean equals(Object obj)
   {
      // if the arriving object is also a SerializableContextual, then unwrap it
      if (obj instanceof SerializableContextualImpl<?, ?>)
      {
         return delegate().equals(((SerializableContextualImpl<?, ?>) obj).get());
      }
      else
      {
         return delegate().equals(obj);
      }
   }
   
   @Override
   public int hashCode()
   {
      return delegate().hashCode();
   }
   
}
