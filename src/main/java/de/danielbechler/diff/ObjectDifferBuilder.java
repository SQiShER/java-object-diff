/*
 * Copyright 2013 Daniel Bechler
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

package de.danielbechler.diff;

import de.danielbechler.diff.bean.BeanDiffer;
import de.danielbechler.diff.collection.CollectionDiffer;
import de.danielbechler.diff.map.MapDiffer;
import de.danielbechler.diff.primitive.PrimitiveDiffer;

/**
 * @author Daniel Bechler
 */
public final class ObjectDifferBuilder {
    private final CategoryService categoryService = new CategoryService();
    private final ComparisonService comparisonService = new ComparisonService();
    private final ReturnableNodeService returnableNodeService = new ReturnableNodeService();
    private final IntrospectionService introspectionService = new IntrospectionService();
    private final InclusionService inclusionService = new InclusionService(categoryService);
    private final CircularReferenceService circularReferenceService = new CircularReferenceService();
    private final Configurable configurable = new Configurable();

    private ObjectDifferBuilder() {
    }

    public static ObjectDiffer buildDefaultObjectDiffer() {
        return startBuilding().build();
    }

    public static ObjectDifferBuilder startBuilding() {
        return new ObjectDifferBuilder();
    }

    public ObjectDiffer build() {
        final DifferProvider differProvider = new DifferProvider();
        final DifferDispatcher differDispatcher = new DifferDispatcher(differProvider, circularReferenceService, circularReferenceService, inclusionService, returnableNodeService);
        differProvider.push(new BeanDiffer(differDispatcher, introspectionService, returnableNodeService, comparisonService, introspectionService));
        differProvider.push(new CollectionDiffer(differDispatcher, comparisonService));
        differProvider.push(new MapDiffer(differDispatcher, comparisonService, returnableNodeService));
        differProvider.push(new PrimitiveDiffer(comparisonService));
        return new ObjectDiffer(differDispatcher);
    }

    public final Configurable configure() {
        return configurable;
    }

    public class Configurable {
        private Configurable() {
        }

        public ReturnableNodeConfiguration filtering() {
            return returnableNodeService;
        }

        public IntrospectionConfiguration introspection() {
            return introspectionService;
        }

        public CircularReferenceConfiguration circularReferenceHandling() {
            return circularReferenceService;
        }

        public InclusionConfiguration inclusion() {
            return inclusionService;
        }

        public ComparisonConfiguration comparison() {
            return comparisonService;
        }

        public CategoryConfiguration categories() {
            return categoryService;
        }
    }
}
