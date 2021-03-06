package net.revenj

import java.time.OffsetDateTime

import net.revenj.extensibility.{Container, InstanceScope}
import net.revenj.patterns.{AggregateDomainEvent, AggregateDomainEventHandler, AggregateRoot, DomainEventHandler}
import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

import scala.util.Try

class ContainerCheck extends Specification with ScalaCheck {
  sequential

  val cl = classOf[ContainerCheck].getClassLoader

  "simple resolutions" >> {
    "types" >> {
      B.counter = 0
      val container = new SimpleContainer(false, cl)
      container.register[A]()
      val tryA1 = container.tryResolve[A]
      tryA1.isSuccess === false
      container.register[B]()
      val tryA2 = container.tryResolve[A]
      tryA2.isSuccess === true
      val a = tryA2.get
      a !== null
      1 === B.counter
    }
    "option" >> {
      B.counter = 0
      val container = new SimpleContainer(false, cl)
      container.register[A]()
      container.register[B]()
      container.register[C]()
      var c = container.resolve[C]
      c !== null
      1 === B.counter
      container.register[D]()
      c = container.resolve[C]
      c !== null
      2 === B.counter
    }
    "generics" >> {
      B.counter = 0
      val container = new SimpleContainer(false, cl)
      container.register[A]()
      container.register[B]()
      container.register[G[_]]()
      val g = container.resolve[G[A]]
      g !== null
      1 === B.counter
      g.instance.isInstanceOf[A] === true
      classOf[A] === g.instance.getClass
    }
    "complex generics" >> {
      B.counter = 0
      val container = new SimpleContainer(false, cl)
      container.register[A]()
      container.register[B]()
      container.register[ComplexGenerics[_, _]]()
      val cg = container.resolve[ComplexGenerics[A, B]]
      cg !== null
      2 === B.counter
      cg.instance1.isInstanceOf[A] === true
      classOf[A] === cg.instance1.getClass
      cg.instance2.isInstanceOf[B] === true
      classOf[B] === cg.instance2.getClass
    }
    "ctor raw generics" >> {
      B.counter = 0
      val container = new SimpleContainer(false, cl)
      container.register[A]()
      container.register[B]()
      container.register[ComplexGenerics[_, _]]()
      container.register[CtorRawGenerics]()
      val cg = container.resolve[CtorRawGenerics]
      cg !== null
      2 === B.counter
      cg.generics.instance1.isInstanceOf[A] === true
      classOf[A] === cg.generics.instance1.getClass
      cg.generics.instance2.isInstanceOf[B] === true
      classOf[B] === cg.generics.instance2.getClass
    }
    "ctor partial generics" >> {
      B.counter = 0
      val container = new SimpleContainer(false, cl)
      container.register[A]()
      container.register[B]()
      container.register[ComplexGenerics[_, _]]()
      container.register[CtorPartialGenerics[_]]()
      val cg = container.resolve[CtorPartialGenerics[A]]
      cg !== null
      2 === B.counter
      cg.generics.instance1.isInstanceOf[A] === true
      classOf[A] === cg.generics.instance1.getClass
      cg.generics.instance2.isInstanceOf[B] === true
      classOf[B] === cg.generics.instance2.getClass
    }
    "pass exception" >> {
      val container = new SimpleContainer(false, cl)
      container.registerFunc[ContainerCheck](_ => throw new RuntimeException("test me now"))
      val cc = container.tryResolve[ContainerCheck]
      cc.isFailure === true
      cc.failed.get.getMessage.contains("test me now") === true
    }
    "resolve unknown" >> {
      B.counter = 0
      val container = new SimpleContainer(true, cl)
      val a = container.resolve[A]
      a !== null
      1 === B.counter
    }
    "resolve later" >> {
      val container = new SimpleContainer(true, cl)
      val l = container.resolve[Later]
      l !== null
      val a = l.getA()
      a !== null
    }
    "self reference singleton" >> {
      val container = new SimpleContainer(false, cl)
      container.register[SelfReference](InstanceScope.Singleton)
      val sr = container.resolve[SelfReference]
      val sr2 = sr.getSelf()
      sr === sr2
    }
    "singleton in context" >> {
      val container = new SimpleContainer(false, cl)
      container.register[Single](InstanceScope.Singleton)
      val nested = container.createScope()
      val s1 = nested.resolve[Single]
      val s2 = container.resolve[Single]
      s1 === s2
    }
    "collection resolution" >> {
      val container = new SimpleContainer(false, cl)
      container.registerAs[DomainEventHandler[Single], Handler1]()
      container.registerAs[DomainEventHandler[Single], Handler2]()
      val found = container.resolve[Array[DomainEventHandler[Single]]]
      found.length === 2
    }
    "collection signature resolution" >> {
      val container = new SimpleContainer(false, cl)
      container.registerAs[DomainEventHandler[Single], Handler1]()
      container.registerAs[DomainEventHandler[Single], Handler2]()
      container.registerAs[DomainEventHandler[Function0[Single]], Handler3]()
      val found1 = container.resolve[Array[DomainEventHandler[Single]]]
      found1.length === 2
      val found2 = container.resolve[Array[DomainEventHandler[Function0[Single]]]]
      found2.length === 1
    }
    "sequence resolution" >> {
      val container = new SimpleContainer(false, cl)
      container.registerAs[DomainEventHandler[Single], Handler1]()
      container.registerAs[DomainEventHandler[Single], Handler2]()
      container.registerAs[DomainEventHandler[Function0[Single]], Handler3]()
      val found = container.resolve[Seq[DomainEventHandler[Single]]]
      found.length === 2
    }
    "complex sequence resolution" >> {
      val container = new SimpleContainer(false, cl)
      container.registerAs[AggregateDomainEventHandler[Agg, AggEvent], AggEventHandler1]()
      container.registerAs[AggregateDomainEventHandler[Agg, AggEvent], AggEventHandler2.type]()
      val found = container.resolve[Seq[AggregateDomainEventHandler[Agg, AggEvent]]]
      found.length === 2
    }
    "factory with singleton" >> {
      val container = new SimpleContainer(false, cl)
      container.registerFunc[Single](_ => new Single, InstanceScope.Singleton)
      val s1 = container.resolve[Single]
      val s2 = container.resolve[Single]
      s1 === s2
    }
    "factory with singleton in scopes" >> {
      val container = new SimpleContainer(false, cl)
      container.registerFunc[Single](_ => new Single, InstanceScope.Singleton)
      val c1 = container.createScope()
      val s1 = c1.resolve[Single]
      val s2 = c1.resolve[Single]
      s1 === s2
      val c2 = container.createScope()
      val s3 = c2.resolve[Single]
      val s4 = c2.resolve[Single]
      s3 === s4
      s1 === s3
    }
    "factory with context in scopes" >> {
      val container = new SimpleContainer(false, cl)
      container.registerFunc[Single](c => new Single, lifetime = InstanceScope.Context)
      val c1 = container.createScope()
      val s1 = c1.resolve[Single]
      val s2 = c1.resolve[Single]
      s1 === s2
      val c2 = container.createScope()
      val s3 = c2.resolve[Single]
      val s4 = c2.resolve[Single]
      s3 === s4
      s1 !== s3
    }
    "circular dependency" >> {
      val container = new SimpleContainer(false, cl)
      container.register[CircularTop](InstanceScope.Context)
      container.register[CircularDep](InstanceScope.Context)
      val tryResolve = Try { container.resolve[CircularTop] }
      tryResolve.isFailure === true
      tryResolve.failed.get.getMessage === "Unable to resolve: class net.revenj.CircularTop. Circular dependencies in signature detected"
    }
    "singleton container" >> {
      val container = new SimpleContainer(false, cl)
      container.register[UsesContainer](InstanceScope.Singleton)
      val scope = container.createScope()
      val uc = scope.resolve[UsesContainer]
      container === uc.container
      scope !== uc.container
    }
    "collection error is propagated" >> {
      val container = new SimpleContainer(false, cl)
      container.register[ErrorInCollection]()
      val tryCol = Try { container.resolve[Array[ErrorInCollection]] }
      tryCol.isFailure === true
      tryCol.failed.get.asInstanceOf[ReflectiveOperationException].getMessage == "Unable to resolve class net.revenj.ErrorInCollection. Error: naah"
    }
  }
}

class A(val b: B)

class B {
  B.counter += 1
}

object B {
  var counter: Int = 0
}

class C(val a: A, val d: Option[D])

class D

class G[T](val instance: T)

class ComplexGenerics[T1, T2](val instance2: T2, val instance1: T1)

class CtorRawGenerics(val generics: ComplexGenerics[A, B])

class CtorPartialGenerics[T](val generics: ComplexGenerics[T, B])

class Later(val aFunction: () => A) {
  def getA() = aFunction.apply()
}

class SelfReference(val self: () => SelfReference) {
  def getSelf() = self.apply()
}

class Single

class Handler1 extends DomainEventHandler[Single] {
  override def handle(domainEvent: Single): Unit = ()
}
class Handler2 extends DomainEventHandler[Single] {
  override def handle(domainEvent: Single): Unit = ()
}
class Handler3 extends DomainEventHandler[Function0[Single]] {
  override def handle(domainEvent: Function0[Single]): Unit = ()
}

class Agg extends AggregateRoot {
  def URI = ???
}
class AggEvent extends AggregateDomainEvent[Agg] {
  def URI = ???
  def queuedAt: OffsetDateTime = ???
  def processedAt: Option[OffsetDateTime] = None

  def apply(aggregate: Agg): Unit = ???
}
class AggEventHandler1 extends AggregateDomainEventHandler[Agg, AggEvent] {
  def handle(domainEvent: AggEvent, aggregate: Agg): Unit = ???
}
object AggEventHandler2 extends AggregateDomainEventHandler[Agg, AggEvent] {
  def handle(domainEvent: AggEvent, aggregate: Agg): Unit = ???
}

class CircularTop(val dep: CircularDep)
class CircularDep(val top: CircularTop)

class UsesContainer(val container: Container)

class ErrorInCollection {
  throw new RuntimeException("naah")
}