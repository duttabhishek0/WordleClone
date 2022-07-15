package com.abhishek.wordleclone.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.abhishek.wordleclone.R
import com.abhishek.wordleclone.WordleApplication
import com.abhishek.wordleclone.databinding.FragmentGameScreenBinding
import com.abhishek.wordleclone.databinding.StatisticDialogBinding
import com.abhishek.wordleclone.domain.WordleDatabase
import com.abhishek.wordleclone.domain.entity.GameStats
import com.abhishek.wordleclone.utils.*
import com.abhishek.wordleclone.viewmodel.Letter
import com.abhishek.wordleclone.viewmodel.WorldeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GameScreenFragment : Fragment() {

    private var _binding: FragmentGameScreenBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<WorldeViewModel>()

    private lateinit var database: WordleDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentGameScreenBinding.inflate(inflater, container, false)

        database = (requireActivity().application as WordleApplication).wordleDatabase

        CoroutineScope(Dispatchers.Default).launch {
            val currentStat = database.gameStatsDao().getStat(1)

            if (currentStat == null) {
                database.gameStatsDao().insertStat((currentStat))
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val listOfTextViews = listOf(
            listOf(
                binding.firstRow1,
                binding.firstRow2,
                binding.firstRow3,
                binding.firstRow4,
                binding.firstRow5
            ),
            listOf(
                binding.secondRow1,
                binding.secondRow2,
                binding.secondRow3,
                binding.secondRow4,
                binding.secondRow5
            ),
            listOf(
                binding.thirdRow1,
                binding.thirdRow2,
                binding.thirdRow3,
                binding.thirdRow4,
                binding.thirdRow5
            ),
            listOf(
                binding.fourthRow1,
                binding.fourthRow2,
                binding.fourthRow3,
                binding.fourthRow4,
                binding.fourthRow5
            ),
            listOf(
                binding.fifthRow1,
                binding.fifthRow2,
                binding.fifthRow3,
                binding.fifthRow4,
                binding.fifthRow5
            ),
            listOf(
                binding.sixthRow1,
                binding.sixthRow2,
                binding.sixthRow3,
                binding.sixthRow4,
                binding.sixthRow5
            )
        )

        val lettersRow = listOf(
            binding.firstLettersRow,
            binding.secondLettersRow,
            binding.thirdLettersRow,
            binding.fourthLettersRow,
            binding.fifthLettersRow,
            binding.sixthLettersRow
        )
        val keyboardMap = mapOf<String, Button>(
            "A" to binding.A,
            "B" to binding.B,
            "C" to binding.C,
            "D" to binding.D,
            "E" to binding.E,
            "F" to binding.F,
            "G" to binding.G,
            "H" to binding.H,
            "I" to binding.I,
            "J" to binding.J,
            "K" to binding.K,
            "L" to binding.L,
            "M" to binding.M,
            "N" to binding.N,
            "O" to binding.O,
            "P" to binding.P,
            "Q" to binding.Q,
            "R" to binding.RR,
            "S" to binding.S,
            "T" to binding.T,
            "U" to binding.U,
            "V" to binding.V,
            "W" to binding.W,
            "X" to binding.X,
            "Y" to binding.Y,
            "Z" to binding.Z,
        )

        var shakeAnimation = shakeAnimation(lettersRow[viewModel.currentPosition.row])

        keyboardMap.forEach { (letter, button) ->
            lifecycleScope.launch {
                viewModel.listOfKeys[letter]!!.collect { key ->
                    button.apply {
                        setBackgroundColor(resources.getColor(key.backgroundColor))
                        setTextColor(resources.getColor(key.textColor))
                    }
                }
            }
            button.setOnClickListener {
                viewModel.setLetter(letter)
            }
        }

        listOfTextViews.forEachIndexed { rows, list ->
            list.forEachIndexed { cols, textView ->
                lifecycleScope.launch {
                    viewModel.listOfTextViews[rows][cols].collect { s ->
                        if (s.letter != " " && s.backgroundColor == R.color.white) {
                            slightlyScaleUpAnimation(textView)
                        }
                        textView.apply {
                            text = s.letter
                            background = resources.getDrawable(s.backgroundColor)
                            setTextColor(resources.getColor(s.textColor))
                        }
                    }
                }
            }
        }

        binding.deleteButton.setOnClickListener {
            viewModel.deleteLetter()
        }

        binding.enterButton.setOnClickListener {
            lifecycleScope.launch {
                viewModel.checkRow()
            }
        }


        val inflater = requireActivity().layoutInflater


        val binderDialog = StatisticDialogBinding.inflate(inflater)
        val builder =
            AlertDialog.Builder(requireContext()).apply {
                setView(binderDialog.root)
            }.create()
        binderDialog.next.setOnClickListener {
            builder.cancel()
        }


        lifecycleScope.launch {
            viewModel.signal.collect {it->
                when (it) {
                    Signal.NOTAWORD -> {
                        showInfo(binding.info, "Not in word list")
                        shakeAnimation()
                    }
                    Signal.NEEDLETTER -> {
                        showInfo(binding.info, "Not enough letters")
                        shakeAnimation()
                    }
                    Signal.NEXTTRY -> {
                        flip(
                            listOfTextViews[viewModel.currentPosition.row],
                            viewModel.checkColor(),
                        ) {
                            viewModel.emitColor()
                            viewModel.currentPosition.nextRow()
                            shakeAnimation =
                                shakeAnimation(lettersRow[viewModel.currentPosition.row])
                        }
                    }
                    Signal.GAMEOVER -> {
                        CoroutineScope(Dispatchers.Default).launch {
                            val currentStat = database.gameStatsDao().getStat(1)
                            currentStat!!.levelFailed()
                            bindDialog(binderDialog, currentStat)
                            database.gameStatsDao().updateStat(currentStat)
                        }
                        showInfo(binding.info, viewModel.wordle)

                        flip(
                            listOfTextViews[viewModel.currentPosition.row],
                            viewModel.checkColor(),
                        ) {
                            builder.show()
                            viewModel.emitColor()
                            viewModel.resetGame()
                            shakeAnimation =
                                shakeAnimation(lettersRow[viewModel.currentPosition.row])
                        }
                    }
                    Signal.WIN -> {
                        val pos = viewModel.currentPosition.row
                        val tws = listOfTextViews[pos]

                        when (pos) {
                            0 -> showInfo(binding.info, "Genius")
                            1 -> showInfo(binding.info, "Magnificent")
                            2 -> showInfo(binding.info, "Impressive")
                            3 -> showInfo(binding.info, "Splendid")
                            4 -> showInfo(binding.info, "Great")
                            5 -> showInfo(binding.info, "Phew")
                        }

                        CoroutineScope(Dispatchers.Default).launch {
                            val currentStat = database.gameStatsDao().getStat(1)
                            currentStat!!.levelCleared(pos)
                            bindDialog(binderDialog, currentStat)
                            database.gameStatsDao().updateStat(currentStat)
                        }
                        flip(
                            tws,
                            viewModel.checkColor(),
                        ) {
                            winAnimator(tws){

                                viewModel.emitColor()
                                viewModel.resetGame()
                                builder.show()
                                shakeAnimation =
                                    shakeAnimation(lettersRow[viewModel.currentPosition.row])
                            }.start()
                        }
                    }
                }
            }
        }
    }

    private fun flip(
        listOfTextViews: List<TextView>,
        letters: List<Letter>,
        reset: Boolean = false,
        doOnEnd: () -> Unit
    ) {
        flipListOfTextViews(
            listOfTextViews,
            letters,
            reset = reset
        ) {
            doOnEnd()
        }.start()
    }

    private fun bindDialog(binding: StatisticDialogBinding, stats: GameStats) {
        binding.apply {
            var totalWin = (stats.alpha + stats.beta + stats.gamma + stats.delta + stats.epsilon + stats.zeta)
            totalWin = if (totalWin == 0) 1 else totalWin

            played.text = stats.gamePlayed.toString()
            winPercentage.text = winRatio(stats).toString()
            currentStreak.text = stats.streak.toString()
            maxStreak.text = stats.maxStreak.toString()

            one.title = "1"
            one.percentage = stats.alpha / totalWin.toFloat()
            one.countText = stats.alpha.toString()

            two.title = "2"
            two.percentage = stats.beta / totalWin.toFloat()
            two.countText = stats.beta.toString()

            three.title = "3"
            three.percentage = stats.gamma / totalWin.toFloat()
            three.countText = stats.gamma.toString()

            four.title = "4"
            four.percentage = stats.delta / totalWin.toFloat()
            four.countText = stats.delta.toString()

            five.title = "5"
            five.percentage = stats.epsilon / totalWin.toFloat()
            five.countText = stats.epsilon.toString()

            six.title = "6"
            six.percentage = stats.zeta/ totalWin.toFloat()
            six.countText = stats.zeta.toString()
        }
    }

    fun winRatio(stats: GameStats): Int {
        val totalWin = (stats.alpha + stats.beta + stats.gamma + stats.delta + stats.epsilon + stats.zeta)
        val ratio = totalWin.toFloat() / stats.gamePlayed
        return (ratio * 100).toInt()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}