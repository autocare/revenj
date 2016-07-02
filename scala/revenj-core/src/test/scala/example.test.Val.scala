package example.test




case class Val @com.fasterxml.jackson.annotation.JsonIgnore() (
	   x: Option[Int] = None,
	   f: Float = 0.0f,
	   ff: Set[Option[Float]] = Set.empty,
	   a: example.test.Another = example.test.Another(),
	   aa: Option[example.test.Another] = None,
	   aaa: Array[Option[example.test.Another]] = Array.empty,
	   aaaa: List[example.test.Another] = List.empty,
	   en: example.test.En = example.test.En.A,
	   en2: Option[example.test.En] = None,
	   en3: scala.collection.mutable.LinkedList[example.test.En] = scala.collection.mutable.LinkedList.empty,
	   i4: scala.collection.mutable.LinkedList[Int] = scala.collection.mutable.LinkedList.empty,
	   another: scala.collection.mutable.LinkedList[example.test.Another] = scala.collection.mutable.LinkedList.empty,
	   d: Option[java.time.LocalDate] = None
	) {
	
		require(x ne null, "Null value was provided for property \"x\"")
		require(ff ne null, "Null value was provided for property \"ff\"")
		net.revenj.Guards.checkCollectionOptionValNulls(ff)
		require(a ne null, "Null value was provided for property \"a\"")
		require(aa ne null, "Null value was provided for property \"aa\"")
		if (aa.isDefined) require(aa.get ne null, "Null value was provided for property \"aa\"")
		require(aaa ne null, "Null value was provided for property \"aaa\"")
		net.revenj.Guards.checkArrayOptionRefNulls(aaa)
		require(aaaa ne null, "Null value was provided for property \"aaaa\"")
		net.revenj.Guards.checkCollectionNulls(aaaa)
		require(en ne null, "Null value was provided for property \"en\"")
		require(en2 ne null, "Null value was provided for property \"en2\"")
		if (en2.isDefined) require(en2.get ne null, "Null value was provided for property \"en2\"")
		require(en3 ne null, "Null value was provided for property \"en3\"")
		net.revenj.Guards.checkCollectionNulls(en3)
		require(i4 ne null, "Null value was provided for property \"i4\"")
		require(another ne null, "Null value was provided for property \"another\"")
		net.revenj.Guards.checkCollectionNulls(another)
		require(d ne null, "Null value was provided for property \"d\"")
		if (d.isDefined) require(d.get ne null, "Null value was provided for property \"d\"")
	
	private var _hasD : Boolean = false
	
	
	@com.fasterxml.jackson.annotation.JsonProperty("hasD")
	def hasD = {
		
		_hasD = Val.hasD(this)
		_hasD
	}

	private var _enSize : Int = 0
	
	
	@com.fasterxml.jackson.annotation.JsonProperty("enSize")
	def enSize = {
		
		_enSize = Val.enSize(this)
		_enSize
	}

}

object Val{

	
			def hasD(it : example.test.Val):Boolean = it.d.isDefined
			def enSize(it : example.test.Val): Int = (it.en3.size)
			
	@com.fasterxml.jackson.annotation.JsonCreator def jackson(
		@com.fasterxml.jackson.databind.annotation.JsonDeserialize(contentAs = classOf[java.lang.Integer]) @com.fasterxml.jackson.annotation.JsonProperty("x") x: Option[Int],
		@com.fasterxml.jackson.annotation.JsonProperty("f") f: Float,
		@com.fasterxml.jackson.annotation.JsonProperty("ff") ff: Set[Option[Float]],
		@com.fasterxml.jackson.annotation.JsonProperty("a") a: example.test.Another,
		@com.fasterxml.jackson.annotation.JsonProperty("aa") aa: Option[example.test.Another],
		@com.fasterxml.jackson.annotation.JsonProperty("aaa") aaa: Array[Option[example.test.Another]],
		@com.fasterxml.jackson.annotation.JsonProperty("aaaa") aaaa: List[example.test.Another],
		@com.fasterxml.jackson.annotation.JsonProperty("en") en: example.test.En,
		@com.fasterxml.jackson.annotation.JsonProperty("en2") en2: Option[example.test.En],
		@com.fasterxml.jackson.annotation.JsonProperty("en3") en3: scala.collection.mutable.LinkedList[example.test.En],
		@com.fasterxml.jackson.annotation.JsonProperty("i4") i4: scala.collection.mutable.LinkedList[Int],
		@com.fasterxml.jackson.annotation.JsonProperty("another") another: scala.collection.mutable.LinkedList[example.test.Another],
		@com.fasterxml.jackson.annotation.JsonProperty("d") d: Option[java.time.LocalDate]) = {
		Val(  x = x, f = f, ff = if (ff == null) Set.empty else ff, a = if (a == null) example.test.Another() else a, aa = aa, aaa = if (aaa == null) Array.empty else aaa, aaaa = if (aaaa == null) List.empty else aaaa, en = if (en == null) example.test.En.A else en, en2 = en2, en3 = if (en3 == null) scala.collection.mutable.LinkedList.empty else en3, i4 = if (i4 == null) scala.collection.mutable.LinkedList.empty else i4, another = if (another == null) scala.collection.mutable.LinkedList.empty else another, d = d)
	}

}