package ru.be_more.orange_forum.presentation.screens.posting

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Icon
import ru.be_more.orange_forum.presentation.screens.base.BaseViewModel

class PostingViewModel(
    val boardId: String,
    val threadNum: Int,
    private val additionalString: String?,
    private val boardInteractor: InteractorContract.BoardInteractor,
    private val replyInteractor: InteractorContract.ReplyInteractor,
) : BaseViewModel() {

    var isOpSelected = MutableStateFlow(false)

    var isSubjectEnabled = MutableStateFlow(false)
    var isSubjectVisible = MutableStateFlow(false)
    var subject = MutableStateFlow("")

    var isEmailVisible = MutableStateFlow(false)
    var email = MutableStateFlow("")
    var isSageEnabled = MutableStateFlow(false)
    var isSageSelected = MutableStateFlow(false)

    var isNameEnabled = MutableStateFlow(false)
    var isNameVisible = MutableStateFlow(false)
    var name = MutableStateFlow("")

    var isTagEnabled = MutableStateFlow(false)
    var isTagVisible = MutableStateFlow(false)
    var tag = MutableStateFlow("")

    var isIconEnabled = MutableStateFlow(false)
    var isIconVisible = MutableStateFlow(false)
    var iconTitle = MutableStateFlow("")
    var iconUrl = MutableStateFlow("")
    private var iconId = MutableStateFlow(-1)

    var comment = MutableStateFlow(additionalString ?: "")

    var captchaUrl = MutableStateFlow("")
    var captcha = MutableStateFlow("")

    var iconList = emptyList<IconListItem>()
        private set
    var isIconListVisible = MutableStateFlow(false)

    init {
        runOnIo {
            captchaUrl.value = getCaptcha().await()
            boardInteractor
                .getBoard(boardId)
                ?.boardSetting
                ?.let { boardSettings ->
                    showContent()
                    isSubjectEnabled.value = boardSettings.isSubjectEnabled
                    isSageEnabled.value = boardSettings.isSageEnabled
                    isNameEnabled.value = boardSettings.isNameEnabled
                    isTagEnabled.value = boardSettings.isTagEnabled
                    isIconEnabled.value = boardSettings.isIconEnabled
                    iconList = boardSettings.icons?.map { icon ->
                        IconListItem(
                            icon = icon,
                            onClick = ::onIconClicked
                        )
                    } ?: emptyList()
                }
        }
    }

    private fun onIconClicked(icon: Icon) {
        iconTitle.value = icon.name
        iconId.value = icon.id
        iconUrl.value = icon.url
        isIconListVisible.value = false
    }

    private fun getCaptcha() =
        viewModelScope.async {
            replyInteractor.getCaptcha(boardId, threadNum)
        }

    fun onSageClick() {
        isSageSelected.value = !isSageSelected.value
        if (isSageSelected.value) {
            isEmailVisible.value = false
            email.value = "sage"
        } else {
            email.value = ""
        }
    }

    fun onEmailClick() {
        isEmailVisible.value = !isEmailVisible.value
    }

    fun onOpClick() {
        isOpSelected.value = !isOpSelected.value
    }

    fun onSubjectClick() {
        isSubjectVisible.value = !isSubjectVisible.value
    }

    fun onNameClick() {
        isNameVisible.value = !isNameVisible.value
    }

    fun onTagClick() {
        isTagVisible.value = !isTagVisible.value
    }

    fun onIconClick() {
        isIconVisible.value = !isIconVisible.value
    }

    fun onSubjectEdit(string: String) {
        subject.value = string
    }

    fun onEmailEdit(string: String) {
        email.value = string
    }

    fun onNameEdit(string: String) {
        name.value = string
    }

    fun onTagEdit(string: String) {
        tag.value = string
    }

    fun onCommentEdit(string: String) {
        comment.value = string
    }

    fun onCaptchaEdit(string: String) {
        captcha.value = string
    }

    fun onIconPickerClicked() {
        isIconListVisible.value = true
    }

    fun onSendClicked() {
        runOnIo {
            if (threadNum > 0)
                replyInteractor
                    .reply(
                        boardId = boardId,
                        threadNum = threadNum,
                        comment = comment.value,
                        isOp = isOpSelected.value,
                        subject = subject.value,
                        email = email.value,
                        name = name.value,
                        tag = tag.value,
                        captchaSolvedString = captcha.value,
                    )
                /* todo navigate back*/
            else
                replyInteractor
                    .createThread(
                        boardId = boardId,
                        comment = comment.value,
                        isOp = isOpSelected.value,
                        subject = subject.value,
                        email = email.value,
                        name = name.value,
                        tag = tag.value,
                        captchaSolvedString = captcha.value,
                    )
            /* todo navigate into thread*/
        }
    }

    fun onIconClear() {
        iconTitle.value = ""
        iconId.value = -1
    }

    fun onIconDismiss() {
        isIconListVisible.value = false
    }

    fun onCaptchaClick() =
        runOnIo {
            showLoading()
            captchaUrl.value = getCaptcha().await()
            showContent()
        }

    data class IconListItem(
        val icon: Icon,
        val onClick: (Icon) -> Unit,
    )
}