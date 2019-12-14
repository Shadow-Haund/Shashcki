package testPackage
import model.Cell
import model.ChessBoardData
import model.Logic
import model.NoSuchDirectionException
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test


class Test {
    private val logic = Logic()
    private val data = ChessBoardData()

    @Test
    fun forException1() {       //направление движения
        var act = Cell()
        var targ = Cell()
        val map = listOf(Pair(42, 25))  // 42-откуда, 25-куда
        act = logic.data.cells[map[0].first]
        targ = logic.data.cells[map[0].second]
        assertThrows(NoSuchDirectionException::class.java) { logic.getDbyTarget(logic.data, act, targ) }
    }

    @Test
    fun forException2() {       //направление движения
        var act = Cell()
        var targ = Cell()
        val map = listOf(Pair(42, 32))
        act = logic.data.cells[map[0].first]
        targ = logic.data.cells[map[0].second]
        assertThrows(NoSuchDirectionException::class.java) { logic.getDbyTarget(logic.data, act, targ) }
    }

    enum class Status {
        BC, // черная клетка
    }

    @Suppress("INACCESSIBLE_TYPE")
    @Test
    fun forKill() {     //убийство шашки
        // e3 = 44 f4 = 37, f6 = 21 g5 = 30,  g3 = 46  h4 = 39, g5 = 30 e3 = 44
        val map = listOf(Pair(44, 37), Pair(21, 30), Pair(46, 39), Pair(30, 44))
        var act = Cell()
        var targ = Cell()
        for (i in map.indices) {
            act = logic.data.cells[map[i].first]
            targ = logic.data.cells[map[i].second]
            logic.getDbyTarget(logic.data, act, targ)
        }
        if (Status.BC.toString() == logic.data.cells[37].status.name) {
            println("Ok")
        }
    }

    @Suppress("INACCESSIBLE_TYPE")
    @Test
    fun turkish() {     // есть две шашки за 1 ход
        //      w            b            w             b            w                b
        // c3=42:d4=35  b6=17:a5=24  b2=49:c3=42   h6=23:g5=30  d4=35:e5=28   f6=21:d4=35:b2=49
        val map = listOf(Pair(42, 35), Pair(17, 24), Pair(49, 42), Pair(23, 30), Pair(35, 28), Pair(21, 35), Pair(35, 49))
        var act = Cell()
        var targ = Cell()
        for (i in map.indices) {
            act = logic.data.cells[map[i].first]
            targ = logic.data.cells[map[i].second]
            logic.userStep(act, targ)
        }
        if (Status.BC.toString() == logic.data.cells[28].status.name && Status.BC.toString() == logic.data.cells[42].status.name) {
            println("Ok")
        }
    }

    @Suppress("INACCESSIBLE_TYPE")
    @Test
    fun noMoveWhileAttack() {   //блокировать ход если есть возможность атаки
        // e3 = 44 f4 = 37, f6 = 21 g5 = 30,  g3 = 46  h4 = 39, g5 = 30 e3 = 44
        val map = listOf(Pair(44, 37), Pair(21, 30), Pair(46, 39), Pair(19, 28))
        var act = Cell()
        var targ = Cell()
        for (i in map.indices) {
//
            act = logic.data.cells[map[i].first]
            targ = logic.data.cells[map[i].second]
            logic.getDbyTarget(logic.data, act, targ)
        }
        if (Status.BC.toString() == logic.data.cells[28].status.name) {
            println("Ok")
        }
    }
}
//    a  b  c  d  e  f  g  h
//8   0  1  2  3  4  5  6  7
//7   8  9 10 11 12 13 14 15
//6  16 17 18 19 20 21 22 23
//5  24 25 26 27 28 29 30 31
//4  32 33 34 35 36 37 38 39
//3  40 41 42 43 44 45 46 47
//2  48 49 50 51 52 53 54 55
//1  56 57 58 59 60 61 62 63