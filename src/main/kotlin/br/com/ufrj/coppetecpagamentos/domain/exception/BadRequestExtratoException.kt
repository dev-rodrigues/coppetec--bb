package br.com.ufrj.coppetecpagamentos.domain.exception

class BadRequestExtratoException(message: String, ag: String, cc: String, de: String, ate: String): RuntimeException(message)