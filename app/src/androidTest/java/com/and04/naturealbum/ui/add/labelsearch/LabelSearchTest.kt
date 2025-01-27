package com.and04.naturealbum.ui.add.labelsearch

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.and04.naturealbum.data.localdata.room.Label
import com.and04.naturealbum.ui.utils.UiState
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LabelSearchTest {

    @get:Rule
    val composeTestRule = createComposeRule()
    private val testUiState: MutableState<LabelSearchUiState> =
        mutableStateOf(LabelSearchUiState.Loading)
    private val editTextQuery: MutableState<QueryLabel> = mutableStateOf(QueryLabel.empty())

    @Before
    fun setup() {
        composeTestRule.setContent {
            LabelSearchScreen(
                uiState = mutableStateOf(UiState.Idle),
                labelsState = testUiState.value,
                onSelected = {},
                queryLabel = editTextQuery,
                onChangeText = {}
            )
        }
    }

    @Test
    fun 로딩_중일_때는_리스트가_노출되지_않는다() {
        testUiState.value = LabelSearchUiState.Loading

        composeTestRule
            .onNodeWithText("test name")
            .assertDoesNotExist()
    }

    @Test
    fun 이전에_등록한_라벨들의_리스트가_노출된다() {
        testUiState.value = LabelSearchUiState.RegisteredLabels(
            listOf(
                Label(
                    id = 1,
                    backgroundColor = getRandomColor(),
                    name = "test name"
                )
            )
        )

        composeTestRule
            .onNodeWithText("test name")
            .assertExists()
    }

    @Test
    fun 라벨을_입력하면_새로운_라벨이_노출된다() {
        testUiState.value = LabelSearchUiState.RegisteredLabels(
            emptyList()
        )
        editTextQuery.value = editTextQuery.value.copy(text = "test")

        composeTestRule
            .onNode(
                hasTestTag("chip") and
                        hasText("test")
            )
            .assertExists()
    }

    @Test
    fun 등록_된_라벨_중_입력_텍스트와_중복되는_라벨들이_노출된다() {
        testUiState.value = LabelSearchUiState.RegisteredLabels(
            listOf(
                Label(
                    id = 1,
                    backgroundColor = getRandomColor(),
                    name = "test name"
                ),
                Label(
                    id = 2,
                    backgroundColor = getRandomColor(),
                    name = "강아지"
                ),
                Label(
                    id = 3,
                    backgroundColor = getRandomColor(),
                    name = "고양이"
                )
            )
        )
        editTextQuery.value = editTextQuery.value.copy(text = "test")

        composeTestRule
            .onNodeWithText("test name")
            .assertExists()
    }

    @Test
    fun 등록_된_라벨_중_입력_텍스트와_중복되지_않은_라벨들은_노출되지_않는다() {
        testUiState.value = LabelSearchUiState.RegisteredLabels(
            listOf(
                Label(
                    id = 1,
                    backgroundColor = getRandomColor(),
                    name = "test name"
                ),
                Label(
                    id = 2,
                    backgroundColor = getRandomColor(),
                    name = "강아지"
                ),
                Label(
                    id = 3,
                    backgroundColor = getRandomColor(),
                    name = "고양이"
                )
            )
        )
        editTextQuery.value = editTextQuery.value.copy(text = "test")

        composeTestRule
            .onNodeWithText("강아지")
            .assertDoesNotExist()
    }
}
