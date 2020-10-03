package ru.be_more.orange_forum.domain.interactors

import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.extentions.disposables

open class BaseInteractorImpl: InteractorContract.BaseInteractor {
    override fun release() {
        disposables.forEach { it.dispose() }
        disposables.clear()
    }

}