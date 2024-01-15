package br.com.ufrj.coppetecpagamentos.domain.model

enum class HttpUri {
    AUTENTICAR,
    CONSULTAR_LOTE,
    CONSULTAR_TRANSFERENCIA,
    CONSULTAR_TRANSFERENCIAS,
    CONSULTAR_EXTRATO,
    ENVIAR_LOTE,
    LIBERAR_LOTE,
}

enum class TipoHeader {
    AUTENTICACAO,
    CONSULTA_AUTENTICADO
}

enum class API {
    TRANSFERENCIA,
    EXTRATO
}