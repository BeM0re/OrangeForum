package ru.be_more.orange_forum.extentions


//Board, thread, post
val String.toChanLink: Triple<String, Int, Int>?
    get() {
        if (this.isEmpty()) return null

        var url = this.substring(1)
        
        //id борды от 1 до 4 символов
        val boardLength = url.indexOf('/')
        if (boardLength == -1) return null
        val board = url.substring(0, boardLength)
        
        //обрезаем id борды и "/res/"
        url = url.substring(boardLength + 5)
        
        val threadLength = url.indexOf('.')
        if (threadLength == -1) return null
        val thread = url.substring(0, threadLength).toInt()

        val sharpInd = url.indexOf('#')
        if (sharpInd == -1) return null
        val post = url.substring(sharpInd + 1).toInt()

        return Triple(board, thread, post)
    }