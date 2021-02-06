package com.example.helloworld

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import java.lang.IndexOutOfBoundsException
import java.util.*

class MainActivity : AppCompatActivity() {
    private var whoseTurn = 0
        set(value) {
            field = value % 2
        }

    private var gameOver = false


    // private var imgViews: Map<String, ImageView> = mapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val winnerTextView: TextView = findViewById(R.id.winnerText)
        val playerTextView: TextView = findViewById(R.id.textView)

        val players = setPlayers()
        val imgViews = getImageViews()
        val boardView: ImageView = findViewById(R.id.gameBoard)


        val sampleTokenView = findViewById<ImageView>(R.id.sampleToken)
        clearBoard(imgViews)


        val gameButtons = getButtons()
        val resetButton = findViewById<Button>(R.id.resetButton)

        val gameBoard = GameBoard(imgViews,players)

        //Button listeners
        for(col in 0..6){
            gameButtons[col].setOnClickListener {
                // whoseTurn should only ever be 0 or 1
                // add piece to col (whoseTurn+1)
                // Prevent whoseTurn from changing if invalid move
                if (gameBoard.addPiece(col, whoseTurn+1)) {
                    if (!gameOver && gameBoard.fourInARow()){
                        // Replace player message with winner message
                        boardView.setColorFilter(Color.argb(50, 10, 10, 10))
                        winnerTextView.visibility = TextView.VISIBLE
                        winnerTextView.text = "PLAYER ${whoseTurn+1} WINS!"
                        playerTextView.visibility = TextView.INVISIBLE
                        sampleTokenView.visibility = ImageView.INVISIBLE
                        resetButton.visibility = Button.VISIBLE
                        gameOver = true
                    }
                    whoseTurn++
                    playerTextView.text = "Player ${whoseTurn+1}"
                    val drawable = players[whoseTurn + 1]
                    if (drawable != null) sampleTokenView.setImageResource(drawable)
                }
            }
        }
        // !!! create reset button !!!
        resetButton.setOnClickListener {
            restart(imgViews, winnerTextView, playerTextView)
            resetButton.visibility = Button.INVISIBLE
            sampleTokenView.visibility = ImageView.VISIBLE
            boardView.setColorFilter(Color.argb(0,0,0,0))
            val drawable = players[whoseTurn+1]
            if (drawable != null) {
                sampleTokenView.setImageResource(drawable)
            }
            gameBoard.reset()
        }


    }

    private fun clearBoard(imgViews: Map<String, ImageView>){
        // Set views as invisible
        for(r in 0..5){
            for(c in 0..6){
                val view = imgViews["$r$c"]
                if (view != null) {
                    view.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun restart(imgViews: Map<String, ImageView>, winnerTextView: TextView, playerTextView: TextView){
        whoseTurn = 0
        gameOver = false

        // Set views as invisible
        clearBoard(imgViews)
        // Set winnerTextView as invisible
        winnerTextView.visibility = TextView.INVISIBLE

        // Set playerTextView as visible
        playerTextView.text = "Player 1"
        playerTextView.visibility = TextView.VISIBLE

    }



    private fun getButtons(): List<Button> {
        return listOf(
            findViewById(R.id.button0),
            findViewById(R.id.button1),
            findViewById(R.id.button2),
            findViewById(R.id.button3),
            findViewById(R.id.button4),
            findViewById(R.id.button5),
            findViewById(R.id.button6))
    }

    private fun setPlayers(): Map<Int,Int> {
        return mapOf(1 to R.drawable.game_piece_red, 2 to R.drawable.game_piece_black)
    }

    private fun getImageViews(): Map<String, ImageView>{
        // "{row}{col}"
        // Initialize table for 6X7
        return mapOf(
            "00" to this.findViewById(R.id.token00),
            "01" to this.findViewById(R.id.token01),
            "02" to this.findViewById(R.id.token02),
            "03" to this.findViewById(R.id.token03),
            "04" to this.findViewById(R.id.token04),
            "05" to this.findViewById(R.id.token05),
            "06" to this.findViewById(R.id.token06),
            "10" to this.findViewById(R.id.token10),
            "11" to this.findViewById(R.id.token11),
            "12" to this.findViewById(R.id.token12),
            "13" to this.findViewById(R.id.token13),
            "14" to this.findViewById(R.id.token14),
            "15" to this.findViewById(R.id.token15),
            "16" to this.findViewById(R.id.token16),
            "20" to this.findViewById(R.id.token20),
            "21" to this.findViewById(R.id.token21),
            "22" to this.findViewById(R.id.token22),
            "23" to this.findViewById(R.id.token23),
            "24" to this.findViewById(R.id.token24),
            "25" to this.findViewById(R.id.token25),
            "26" to this.findViewById(R.id.token26),
            "30" to this.findViewById(R.id.token30),
            "31" to this.findViewById(R.id.token31),
            "32" to this.findViewById(R.id.token32),
            "33" to this.findViewById(R.id.token33),
            "34" to this.findViewById(R.id.token34),
            "35" to this.findViewById(R.id.token35),
            "36" to this.findViewById(R.id.token36),
            "40" to this.findViewById(R.id.token40),
            "41" to this.findViewById(R.id.token41),
            "42" to this.findViewById(R.id.token42),
            "43" to this.findViewById(R.id.token43),
            "44" to this.findViewById(R.id.token44),
            "45" to this.findViewById(R.id.token45),
            "46" to this.findViewById(R.id.token46),
            "50" to this.findViewById(R.id.token50),
            "51" to this.findViewById(R.id.token51),
            "52" to this.findViewById(R.id.token52),
            "53" to this.findViewById(R.id.token53),
            "54" to this.findViewById(R.id.token54),
            "55" to this.findViewById(R.id.token55),
            "56" to this.findViewById(R.id.token56))
    }


}

class GameBoard(views: Map<String,ImageView>,players: Map<Int,Int>){
    private val board = createBoard() // List of Columns
    private val views = views
    private val players = players

    class Column{
        val stack = mutableListOf(0,0,0,0,0,0)
        private var index = 0

        fun addPiece(piece: Int): Int {
            return if (index <6) {
                stack[index] = piece
                index++
                index - 1
            }
            else {
                // !!! add recycle view thing here !!!
                -1
            }
        }

        fun reset() {
            index = 0
            for (i in stack.indices) stack[i] = 0
        }
    }

    fun reset(){
        // Resets all values in 2D array to 0
        for (col in board) {
            col.reset()
        }
    }

    private fun createBoard(): MutableList<Column>{
        var board = mutableListOf<Column>()
        for (i in 1..7) {
            var col = Column()
            board.add(col)
        }
        return board
    }

    fun addPiece(col: Int, player: Int): Boolean {
        // update 2D array
        val row = board[col].addPiece(player)

        // update display
        if (row >= 0){
            val view = views["$row$col"]
            val drawable = players[player]
            if (view != null && drawable != null) {
                view.setImageResource(drawable)
                view.visibility = View.VISIBLE
                return true
            }
        }
        return false
    }

    fun fourInARow(): Boolean {
        /* Search 2D array left to right, bottom to top
           1) Is any token in slot
           2) Check 8 directions
           3) Pursue valid directions
        */

        // Loop through columns
        for (i in 0 until board.size) {

            for (j in 0 until board[i].stack.size) {
                // Check if not empty
                if (getValue(i,j) == 0) break
                if (checkAllAdjacent(i,j)) return true
            }
        }
        return false
    }

    private fun getValue(x: Int, y: Int): Int {
        return try{
            board[x].stack[y]
        }
        catch (e: IndexOutOfBoundsException){
            0
        }
    }

    private fun check(x: Int,y: Int,dx: Int,dy: Int): Boolean {
        // dx, dy == (-1 or 0 or 1)
        for (i in 1..4){
            if (!checkAdjacent(x,y,x+(i*dx),y+(i*dy))) {
                break
            }
            else if (i == 3) {
                return true
            }
        }
        return false
    }

    private fun checkAdjacent(x: Int,y: Int, x1: Int, y1: Int): Boolean {
        return (getValue(x,y) == getValue(x1,y1))
    }

    private fun checkAllAdjacent(x: Int,y: Int): Boolean {
        return (
            check(x,y,0,1) or
            check(x,y,1,1) or
            check(x,y,1,0) or
            check(x,y,1,-1) or
            check(x,y,0,-1) or
            check(x,y,-1,-1) or
            check(x,y,-1,0) or
            check(x,y,-1,1))


    }

}