package com.commitAttack.web.exception

open class ApiException(errorTitle: ErrorTitle, message: String?) : RuntimeException() {
    var errorTitle = errorTitle
        private set
    override var message: String? = message
        protected set

    init {
        if (message == null) {
            this.message = errorTitle.message
        }
    }

    constructor(errorTitle: ErrorTitle) : this(errorTitle, null)

}