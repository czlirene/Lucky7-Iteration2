package test.NestedFooTests.Foobar;

import test.NestedFooTests.Foobar.Foo.FUBAR;

// test.NestedFooTests.Foobar.Foo
// Ref count = 3

// test.NestedFooTests.Foobar.Bar
// Dec count = 1
// ref count = 1

// test.NestedFooTests.Foobar.Boo
// ref count = 1

// test.NestedFooTests.Foobar.Foo.FUBAR
// ref count = 2

@FUBAR {}
public class Bar extends Foo{
    public Bar(Boo fubu){
    }

    public Foo Foo(){
        return new Foo();
    }
}