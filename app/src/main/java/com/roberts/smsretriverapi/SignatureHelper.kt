package com.roberts.smsretriverapi

import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

private const val TAG = "AppSignatureHelper"

class SignatureHelper(context: Context?) :
    ContextWrapper(context) {
    val appSignature: ArrayList<String>
        get() {
            val appCodes = ArrayList<String>()
            try {
                val myPackageName = packageName
                val myPackageManager = packageManager
                val signatures =
                    myPackageManager.getPackageInfo(
                        myPackageName,
                        PackageManager.GET_SIGNATURES
                    ).signatures

                for (signature in signatures) {
                    val hash =
                        hash(myPackageName, signature.toCharsString())
                    if (hash != null) {
                        appCodes.add(String.format("%s", hash))
                    }
                }
            } catch (e: PackageManager.NameNotFoundException) {
                Log.d(
                    TAG,
                    "Package not found",
                    e
                )
            }
            return appCodes
        }

    companion object {
        private const val HASH_TYPE = "SHA-256"
        const val HASHED_BYTES = 9
        const val BASE64_CHAR = 11
        private fun hash(pkgName: String, signature: String): String? {
            val appInfo = "$pkgName $signature"
            try {
                val messageDigest =
                    MessageDigest.getInstance(HASH_TYPE)
                messageDigest.update(appInfo.toByteArray(StandardCharsets.UTF_8))
                var myHashSignature = messageDigest.digest()
                myHashSignature = Arrays.copyOfRange(
                    myHashSignature,
                    0,
                    HASHED_BYTES
                )
                var base64Hash = Base64.encodeToString(
                    myHashSignature,
                    Base64.NO_PADDING or Base64.NO_WRAP
                )
                base64Hash = base64Hash.substring(0, BASE64_CHAR)
                Log.d(
                    TAG, String.format("pkg: %s -- hash: %s", pkgName, base64Hash)
                )
                return base64Hash
            } catch (error: NoSuchAlgorithmException) {
                Log.e(TAG, "Algorithm not Found", error)
            }
            return null
        }
    }
}
