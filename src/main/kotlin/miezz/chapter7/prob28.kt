package miezz.chapter7

import java.math.BigInteger

fun gcd(a: BigInteger, b: BigInteger): BigInteger {
	return if (b == BigInteger.ZERO)
		a
	else
		gcd(b, a.mod(b))
}

fun multiplicativeInverse(a: BigInteger, n: BigInteger): BigInteger {
	val (x, _) = fullGcd(a, n)
	return if (x > BigInteger.ZERO) x else x + n
}

fun fullGcd(a: BigInteger, b: BigInteger): Pair<BigInteger, BigInteger> {
	return if (b == BigInteger.ZERO) {
		Pair(BigInteger.ONE, BigInteger.ZERO)
	} else {
		val (x, y) = fullGcd(b, a.mod(b))
		Pair(y, x - (a / b) * y)
	}
}

fun calcKeys(p: BigInteger, q: BigInteger): RSAKeys {
	val N = p * q
	val N_prime = (p - BigInteger.ONE) * (q - BigInteger.ONE)
	var e = BigInteger("2")
	while (gcd(e, N_prime) != BigInteger.ONE) {
		e = e.nextProbablePrime()
	}
	val d = multiplicativeInverse(e, N_prime)
	return RSAKeys(e, d, N)
}

fun power(x: BigInteger, n: BigInteger, p: BigInteger): BigInteger {
	if (n == BigInteger.ZERO)
		return BigInteger.ONE
	var tmp = power((x * x).mod(p), n / BigInteger("2"), p)

	if (n.mod(BigInteger("2")) != BigInteger.ZERO)
		tmp = (tmp * x).mod(p)
	return tmp
}

fun encrypt(msg: BigInteger, e: BigInteger, N: BigInteger): BigInteger {
	assert(msg < N)
	return power(msg, e, N)
}

fun decrypt(encrypted: BigInteger, d: BigInteger, N: BigInteger): BigInteger {
	assert(encrypted < N)
	return power(encrypted, d, N)
}

data class RSAKeys(val e: BigInteger, val d: BigInteger, val N: BigInteger)

fun main(args: Array<String>) {
	val keys = calcKeys(BigInteger("127"), BigInteger("211"))
	val msg = BigInteger("12346")
	println(msg)
	val encrypted = encrypt(msg, keys.e, keys.N)
	println(encrypted)
	val decrypted = decrypt(encrypted, keys.d, keys.N)
	println(decrypted)
}
