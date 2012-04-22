package de.danielbechler.diff;

import de.danielbechler.diff.accessor.*;
import de.danielbechler.diff.node.*;
import de.danielbechler.util.Collections;

import java.util.*;

/** @author Daniel Bechler */
@SuppressWarnings({"unchecked"})
public final class CollectionDiffer extends AbstractObjectDiffer
{
	private final ObjectDiffer parentDiffer;

	public CollectionDiffer(final BeanDiffer parentDiffer)
	{
		super(parentDiffer);
		this.parentDiffer = parentDiffer;
	}

	public <T> CollectionNode<T> compare(final Collection<T> workingCollection,
										 final Collection<T> baseCollection,
										 final Collection<T> defaultCollection,
										 final Accessor<Collection<T>> accessor)
	{
		if (workingCollection != null && baseCollection == null)
		{
			final CollectionNode<T> difference = new CollectionNode<T>(accessor);
			difference.setType(DifferenceType.ADDED);
			for (final T item : workingCollection)
			{
				addDifferenceForAddedItem(difference, accessor, workingCollection, item);
			}
			return difference;
		}
		else if (workingCollection == null && baseCollection != null)
		{
			final CollectionNode<T> difference = new CollectionNode<T>(accessor);
			difference.setType(DifferenceType.REMOVED);
			for (final T item : baseCollection)
			{
				addDifferenceForRemovedItem(difference, accessor, baseCollection, item);
			}
			return difference;
		}
		else if (workingCollection == null) // both null
		{
			final CollectionNode<T> difference = new CollectionNode<T>(accessor);
			difference.setType(DifferenceType.UNTOUCHED);
			return difference;
		}
		else
		{
			return handleItemDifferences(workingCollection, baseCollection, defaultCollection, accessor);
		}
	}

	private <T> boolean addDifferenceForRemovedItem(final CollectionNode<T> difference,
													final Accessor<Collection<T>> accessor,
													final Collection<T> collection,
													final T item)
	{
//		difference.indexItem(item);
		final Accessor<T> itemAccessor = difference.accessorForItem(item);
		final Accessor<T> chainedAccessor = new ChainedAccessor(accessor, itemAccessor);
		final DiffNode compare = parentDiffer.compare(null, collection, null, chainedAccessor);
		if (compare != null && compare.getType() != DifferenceType.UNTOUCHED)
		{
			difference.addChild(compare);
			return true;
		}
		return false;
	}

	private <T> boolean addDifferenceForAddedItem(final CollectionNode<T> difference,
												  final Accessor<Collection<T>> accessor,
												  final Collection<T> collection,
												  final T item)
	{
//		difference.indexItem(item);
		final Accessor<T> itemAccessor = difference.accessorForItem(item);
		final Accessor<T> chainedAccessor = new ChainedAccessor(accessor, itemAccessor);
		final DiffNode compare = parentDiffer.compare(collection, null, null, chainedAccessor);
		if (compare != null && compare.getType() != DifferenceType.UNTOUCHED)
		{
			difference.addChild(compare);
			return true;
		}
		return false;
	}

	private <T> CollectionNode<T> handleItemDifferences(final Collection<T> workingCollection,
														final Collection<T> baseCollection,
														final Collection<T> defaultCollection,
														final Accessor<Collection<T>> accessor)
	{
		final CollectionNode<T> difference = new CollectionNode<T>(accessor);
		difference.setType(DifferenceType.UNTOUCHED);
		final Collection<? extends T> added = Collections.filteredCopyOf(workingCollection, baseCollection);
		for (final T item : added)
		{
			if (addDifferenceForAddedItem(difference, accessor, workingCollection, item))
			{
				difference.setType(DifferenceType.CHANGED);
			}
		}
		final Collection<? extends T> removed = Collections.filteredCopyOf(baseCollection, workingCollection);
		for (final T item : removed)
		{
			if (addDifferenceForRemovedItem(difference, accessor, baseCollection, item))
			{
				difference.setType(DifferenceType.CHANGED);
			}
		}
		final Collection<T> changed = new ArrayList<T>(workingCollection);
		changed.removeAll(added);
		changed.removeAll(removed);
		for (final T item : changed)
		{
//			difference.indexItem(item);
			final Accessor<T> itemAccessor = difference.accessorForItem(item);
			final Accessor<T> chainedAccessor = new ChainedAccessor(accessor, itemAccessor);
			final DiffNode itemDifference = parentDiffer.compare(
					workingCollection,
					baseCollection,
					defaultCollection,
					chainedAccessor);
			if (itemDifference.getType() != DifferenceType.UNTOUCHED)
			{
				difference.addChild(itemDifference);
				difference.setType(DifferenceType.CHANGED);
			}
		}
		return difference;
	}
}
