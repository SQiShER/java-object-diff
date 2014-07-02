# Contributing

You discovered a bug or have an idea for a new feature? Great, why don't you send me a pull 
request so everyone can benefit from it?

Getting started is easy:

* Fork the java-object-diff repository on Github.
* Clone the forked repository to your computer.
* Switch to the root project directory and run `mvn clean package`

If everything went well, this should build, test and package the project. Now you can start making your changes.
  
There are some things to help you getting started:

* Make yourself familiar with the [anatomy of java-object-diff](https://github.com/SQiShER/java-object-diff/wiki/The-Anatomy-of-Java-Object-Diff), so you understand the basic architecture.
* [Check for open issues](https://github.com/SQiShER/java-object-diff/issues) that interest you or look for issues with the [_Contributor Friendly_](https://github.com/SQiShER/java-object-diff/issues?labels=Contributor+Friendly&page=1&state=open) tag. These issues are especially well suited to get more familiar with the codebase without being overwhelming.
* In case you have an idea for a new feature, check the issue tracker to see if there were already some discussions regarding that feature. If not, fell free to open a new discussion to see what others think about it.

So you found something you want to work on. That's great! If you run into any problems or are not entirely sure how to tackle the problem, feel free to just ask me on [Twitter](https://twitter.com/SQiShER) or post your question to the [issue tracker](https://github.com/SQiShER/java-object-diff/issues) if it is too big for Twitter.

Before you submit your pull request with the result, please make sure to:

* Write at least one fully integrated test (no mocks and comparison done via public API) to show 
that the fix or feature works as promised - from a user perspective. What you are doing here is 
basically saying: "In case of X you can expect the library to behave like Y". You shouldn't cover 
every possible execution path this way but rather focus on proving that the feature works and under 
which circumstances it will take effect. Doing this will help others to keep your feature intact, 
when the library evolves.	
* Write unit tests! Lots of them! Keep them small and readable and try to cover as much of your logic as possible. But don't go overboard: focus on actual logic and avoid testing simple getters and setters just to reach the magical 100% test coverage.
* Write your tests with Groovy and [Spock](http://spock-framework.readthedocs.org/en/latest/data_driven_testing.html#introduction)! 
Spock is an amazing testing framework that makes many things much, much easier to accomplish. 
It's not hard to learn and a lot of fun to use. Yes, I know that there are still some TestNG tests 
in the codebase, but they are getting replaced one by one until the dependency can finally be removed.

When you've done that, nothing should hold you back from sending me a pull request and bug me until it gets merged and published. :wink:

Thanks for your support and happy coding!