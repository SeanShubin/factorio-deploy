package com.seanshubin.factorio.deploy

object FactorioUtil{
    fun factorioUriToFileName(s:String):String {
        val pattern = """https://dcdn\.factorio\.com/releases/(.*)\?key=.*"""
        val regex = Regex(pattern)
        val match = regex.matchEntire(s)
        if(match == null){
            throw RuntimeException("Value '$s' did not match pattern '$pattern'")
        } else {
            return match.groupValues[1]
        }
    }

}