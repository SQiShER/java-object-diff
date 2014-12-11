/*
 * Copyright 2014 Daniel Bechler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.danielbechler.diff.introspection;

import java.lang.reflect.Method;

/**
 * @author Daniel Bechler
 */
final class PropertyAccessorBuilder {
   private PropertyAccessorBuilder() {
   }

   public static NameAndType forPropertyOf(final Class<?> targetType) {
      final Builder builder = new Builder();
      builder.targetType = targetType;
      return builder;
   }

   public interface Buildable {
      PropertyAccessor build();
   }

   public interface NameAndType {
      ReadOnly property(String name, Class<?> type);
   }

   public interface ReadOnly extends Buildable {
      Buildable readOnly(boolean readOnly);
   }

   public static class Builder implements NameAndType, ReadOnly, Buildable {
      private String propertyName;
      private Class<?> propertyType;
      private Class<?> targetType;
      private boolean readOnly;

      public ReadOnly property(final String name, final Class<?> type) {
         this.propertyName = name;
         this.propertyType = type;
         return this;
      }

      public Buildable readOnly(final boolean readOnly) {
         this.readOnly = readOnly;
         return this;
      }

      public PropertyAccessor build() {
         try {
            final Method readMethod = targetType.getDeclaredMethod(name("get"));
            final Method writeMethod;
            if (readOnly) {
               writeMethod = null;
            } else {
               writeMethod = targetType.getDeclaredMethod(name("set"), propertyType);
            }
            return new PropertyAccessor(propertyName, null, readMethod, writeMethod);
         } catch (final NoSuchMethodException e) {
            throw new RuntimeException(e);
         }
      }

      private String name(final String prefix) {
         return prefix + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
      }
   }
}
