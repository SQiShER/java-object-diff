# (WORK IN PROGRESS)

* Given an Object Differ
	* when comparing BASE(`[a, b]`) to WORKING(`[b, a]`)
		* it should detect that `a` has moved behind `b`
		* it should detect that `b` has moved before `a`
		* when merging the result into List `[c]`
			* it should produce a List equal to `[c, b, a]`
	* when comapring base List `[a, a]` to working List `[a]`
		* it should detect that one `a` has been removed
		* it should detect that the other `a` is still there
		* when merging the result into List `[b, a, c]`
			* it should produce a List equal to `[b, a, c]`
		* when merging the result into List `[b, a, a, c]`
			* it should produce a List equal to `[b, a, c]`
	* when comparing base List `[a, b, a, b, a, a, b]` working List `[a, b, a, b, b, a, a, b]`
		* it should detect that the second subsequence `[a, b]` has changed to `[a, b, b]`
		* when merging the result into the base List
			* is should insert the new `b` right behind the second subsequence `[a, b]`
	* when comapring base List `[a]` to working List `[a, a]`
		* it should detect that one `a` has been added
		* it should detect that there are two `a`'s
	* when merging the diff of Lists `[a, b, c]` and `[c, b, a]` into an empty List
		* it should produce a List equal to `[c, b, a]`
	* when merging the diff of Lists `[a, b, c]` and `[c, b, a]` into List `[b]`
		* it should produce a List equal to `[c, b, a]`
	* when merging the diff of Lists `[a, b, c]` and `[c, b, a]` into List `[b, b, a]`
		* it should produce a List equal to `[b, c, b, a]`
	* when merging the diff of List `[a]` and `[a, a]` two times into List `[a]`
		* should it produce a List equal to `[a, a]`?
		* should it produce a List equal to `[a, a, a]`?
		
It looks like the merging strategy depends on what the user needs and there is no one-size-fits-all solution. The visitor pattern doesn't really allow for idempotent List merges. How could one realize idempotent merging?

* A visitor that captures the items to merge which offers a method, that'll merge based on the items in the target List.
* The ListNode could offer a merge method that only affects items from the working and base version of the diffed object.

What if idempotent merging isn't desired?

* The visitor approach could be used to implement custom merging strategies

I think it makes sense to implement the built-in idempotent merging strategy based on the visitor pattern, to allow the user to replace it with his custom logic.