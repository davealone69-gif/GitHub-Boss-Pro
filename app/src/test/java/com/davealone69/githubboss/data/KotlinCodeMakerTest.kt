package com.davealone69.githubboss.data

import org.junit.Assert.assertTrue
import org.junit.Test

class KotlinCodeMakerTest {
    @Test
    fun generatedFormConcatenatesSubmittedValueAtRuntime() {
        val generated = KotlinCodeMaker.generate("login form")
            .single { it.name == "LoginFormViewModel.kt" }

        assertTrue(
            "Generated Kotlin must interpolate the submitted value at runtime",
            generated.content.contains("message = \"Submitted: \" + text"),
        )
    }

    @Test
    fun offlineGeneratorProducesUsableKotlinWithoutApiKeys() {
        val files = KotlinCodeMaker.generate("simple list")

        assertTrue(files.any { it.name == "SimpleListScreen.kt" })
        assertTrue(files.all { it.content.isNotBlank() })
    }
}
