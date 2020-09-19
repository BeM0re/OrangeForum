package ru.be_more.orange_forum.domain.interactors

import android.util.Log
import ru.be_more.orange_forum.domain.InteractorContract
import ru.be_more.orange_forum.extentions.disposables

open class BaseInteractorImpl: InteractorContract.BaseInteractor {
    override fun release() {
        disposables.forEach { it.dispose() }
        disposables.clear()
    }

}