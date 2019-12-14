package model;

import java.util.ArrayList;
import java.util.Random;

import static model.Logic.Action.FIGHT;
import static model.Logic.Action.MOVE;
import static model.Logic.Direction.*;
import static model.Logic.Player.BLACK;
import static model.Logic.Player.WHITE;


public class Logic {

    public ChessBoardData data;
    boolean steelFighterFlag = false;
    private String userResultCheckersNum;
    private ArrayList<Cell> turkishArr = new ArrayList<Cell>();

    enum Direction {

        RU(1, -1),      // направления движения для белых
        RB(1, 1),       // черных
        LB(-1, 1),      // черных
        LU(-1, -1);     // белых
        private final int kX;
        private final int kY;

        boolean isWhiteD() {
            return (this == RU || this == LU);
        }

        boolean isBlackD() {
            return (this == RB || this == LB);
        }

        boolean isExist() {
            return (this == RU || this == LU || this == RB || this == LB);
        }

        Direction(int kX, int kY) {
            this.kX = kX;
            this.kY = kY;
        }
    }

    enum Action {

        MOVE, FIGHT;

        boolean isFight() {
            return this == FIGHT;
        }

        boolean isMove() {
            return this == MOVE;
        }
    }

    enum Player {

        WHITE, BLACK;

        boolean isWhite() {
            return this == WHITE;
        }

        boolean isBlack() {
            return this == BLACK;
        }
    }

    public void compStep(ChessBoardData data) {
        try {
            Player computer;
            if ((!data.whiteIsHuman) && data.whiteSessionContinue) {    // определяет когда ходить компьютеру
                computer = WHITE;
            } else if ((!data.blackIsHuman) && data.blackSessionContinue) {
                computer = BLACK;
            } else {
                throw new PlayerErrorException();
            }
            if (data.whiteSessionContinue) {                            // сообщения действий компьютера
                data.resultBuf = data.stepWhiteText + "\n";
            } else {
                data.resultBuf = data.stepBlackText + "\n";
            }

            Cell activeCell;
            Cell targetCell;
            Cell victimCell;
            // Сначала битва
            if (getSome(this.data, FIGHT, computer).isExist()) {
                do {
                    Thread.currentThread().sleep(500);
                    Cell actCells[] = getBestEnemy(computer);   // получение лучшей цели
                    activeCell = actCells[0];
                    targetCell = actCells[1];
                    victimCell = getVictim(this.data, activeCell, targetCell, getDbyTarget(this.data, activeCell, targetCell));
                    turkishArr.add(victimCell);     // турейцкий ход, взятие нескольких шашек если такая возможность есть
                    victimCell.setTurkichChecker();
                    activeCell.resetActive();
                    targetCell.setStatus(activeCell.getStatus());
                    activeCell.setBlackCell();
                    checkSetQeen(targetCell);
                    activeCell = targetCell;
                    if (!steelFighterFlag) {
                        data.resultBuf += actCells[0].index + ":" + actCells[1].index;  // вывод данных на конец битвы
                    } else {
                        data.resultBuf += ":" + activeCell.index;   // продолжения битвы
                    }
                    if (isSome(this.data, activeCell, FIGHT)) {     // бой еще продолжается
                        activeCell.setActive();
                        steelFighterFlag = true;
                    } else {
                        activeCell.resetActive();
                        steelFighterFlag = false;
                        resetTurkishArr();
                        changeSession();
                        data.resultBuf += "\n";
                        customResult();
                        return;
                    }
                } while (steelFighterFlag);
            } else {
                // если нет возможности битвы то просто ходьба
                Cell actCells[] = getBestEnemy(computer);   // получение возможного врага
                activeCell = actCells[0];
                targetCell = actCells[1];
                Thread.currentThread().sleep(500);
                targetCell.setStatus(activeCell.getStatus());
                targetCell.resetActive();
                checkSetQeen(targetCell);
                activeCell.setBlackCell();
                data.resultBuf += activeCell.index + ":" + targetCell.index + "\n";
                customResult();
                changeSession();
                return;
            }
        } catch (PlayerErrorException e) {
            e.printStackTrace();
            return;
        } catch (NoSuchDirectionException e) {
            e.printStackTrace();
            return;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void userStep(Cell activeCell, Cell targetCell) {    // определяет ходы игрока человека
        try {
            Player player;
            if (activeCell.isWhite()) {
                player = WHITE;
                if (!steelFighterFlag) {
                    data.resultBuf = data.stepWhiteText + "\n";
                }
            } else if (activeCell.isBlack()) {
                player = BLACK;
                if (!steelFighterFlag) {
                    data.resultBuf = data.stepBlackText + "\n";
                }
            } else {
                throw new PlayerErrorException();
            }

            Direction d = getDbyTarget(this.data, activeCell, targetCell);  // направление с целью атаковать

            if (getSome(this.data, FIGHT, player).isExist() && !isSome(this.data, activeCell, FIGHT)) {
                data.resultBuf += data.userHasFighterText + " " + getSome(this.data, FIGHT, player).index + "\n";
                customResult();
                return;
            }
            // сначало битва
            if (isRightActByD(this.data, activeCell, targetCell, d, FIGHT)) {
                Cell victimCell = getVictim(this.data, activeCell, targetCell, d);
                turkishArr.add(victimCell);
                victimCell.setTurkichChecker();
                activeCell.resetActive();
                targetCell.setStatus(activeCell.getStatus());
                activeCell.setBlackCell();
                checkSetQeen(targetCell);
                if (!steelFighterFlag) {
                    userResultCheckersNum = activeCell.index;
                }
                activeCell = targetCell;
                if (isSome(this.data, activeCell, FIGHT)) {
                    steelFighterFlag = true;
                    activeCell.setActive();
                    userResultCheckersNum += ":" + targetCell.index;
                    return;
                } else {
                    activeCell.resetActive();
                    resetTurkishArr();
                    data.resultBuf += userResultCheckersNum + ":" + targetCell.index + "\n";
                    userResultCheckersNum = "";
                    customResult();
                    steelFighterFlag = false;
                    changeSession();
                    return;
                }
            }

            // кого надо убить
            if (isSome(this.data, activeCell, FIGHT)) {
                data.resultBuf += data.userMustFightText + "\n";
                return;
            }

            // если убивать уже некого, то можно ходить
            if (isRightActByD(this.data, activeCell, targetCell, d, MOVE)) {
                targetCell.setStatus(activeCell.getStatus());
                targetCell.resetActive();
                checkSetQeen(targetCell);
                activeCell.setBlackCell();
                data.resultBuf += activeCell.index + ":" + targetCell.index + "\n";
                customResult();
                changeSession();
                return;
            }

        } catch (PlayerErrorException e) {
            e.printStackTrace();
            return;
        } catch (NoSuchDirectionException e) {
            e.printStackTrace();
            return;
        }
    }

    private void changeSession() {
        data.whiteSessionContinue = !data.whiteSessionContinue;
        data.blackSessionContinue = !data.blackSessionContinue;
    }

    private Cell getVictim(ChessBoardData xData, Cell activeCell, Cell targetCell, Direction d) {
        // функция можно ли шашку впринципе убить
        if (activeCell.isChecker()) {
            if (targetCell.equals(getCellByD(xData, activeCell, d, 2))) {
                return getCellByD(xData, activeCell, d, 1);
            }
        }
        if (activeCell.isQueen()) {
            int deep = 1;
            while (getCellByD(xData, activeCell, d, deep).isBlackCell()) {
                deep++;
            }
            if (getCellByD(xData, activeCell, d, deep).isOpposite(activeCell)) {
                if (getCellByD(xData, activeCell, d, deep + 1).isBlackCell()) {
                    return getCellByD(xData, activeCell, d, deep);
                }
            }
        }

        return new Cell();
    }

    private Cell[] getBestEnemy(Player computer) throws NoSuchDirectionException {
        Action act;
        ArrayList<Cell> variants = new ArrayList<Cell>();
        Cell activeCell = new Cell();
        if (computer.isWhite()) {
            for (int i = 0; i < data.cells.length; i++) {
                if (data.cells[i].isWhiteActiveChecker() || data.cells[i].isWhiteActiveQueen()) {
                    activeCell = data.cells[i];
                }
            }
        } else if (computer.isBlack()) {
            for (int i = 0; i < data.cells.length; i++) {
                if (data.cells[i].isBlackActiveChecker() || data.cells[i].isBlackActiveQueen()) {
                    activeCell = data.cells[i];
                }
            }
        }
        if (activeCell.isExist()) {
            act = FIGHT;
        } else {
            if (getSome(this.data, FIGHT, computer).isExist()) {
                act = FIGHT;
            } else {
                act = MOVE;
            }
        }

        if (activeCell.isExist()) {
            variants.add(activeCell);
        } else {
            if (computer.isWhite()) {
                for (int i = 0; i < data.cells.length; i++) {
                    if (isSome(this.data, data.cells[i], act) && data.cells[i].isWhite()) {
                        variants.add(data.cells[i]);
                    }
                }
            } else {
                for (int i = 0; i < data.cells.length; i++) {
                    if (isSome(this.data, data.cells[i], act) && data.cells[i].isBlack()) {
                        variants.add(data.cells[i]);
                    }
                }
            }
        }

        Cell targets[][] = new Cell[variants.size()][];
        int points[][] = new int[variants.size()][];


        for (int v = 0; v < variants.size(); v++) {
            ArrayList<Cell> tmpTargets = new ArrayList<Cell>();
            for (Direction d : Direction.values()) {
                int deep = 1;
                while (getCellByD(this.data, variants.get(v), d, deep).isExist()) {
                    Cell targetCell = getCellByD(this.data, variants.get(v), d, deep);
                    if (isRightActByD(this.data, variants.get(v), targetCell, d, act)) {
                        tmpTargets.add(targetCell);
                    }
                    deep++;
                }
            }
            targets[v] = tmpTargets.toArray(new Cell[tmpTargets.size()]);
            points[v] = new int[tmpTargets.size()];
        }

        int minIndex = -1;
        int maxIndex = 9;
        for (int v = 0; v < variants.size(); v++) {
            if (variants.get(v).isWhiteChecker() && Integer.parseInt(variants.get(v).index.substring(1)) > minIndex) {
                minIndex = Integer.parseInt(variants.get(v).index.substring(1));
            }
            if (variants.get(v).isBlackChecker() && Integer.parseInt(variants.get(v).index.substring(1)) < maxIndex) {
                maxIndex = Integer.parseInt(variants.get(v).index.substring(1));
            }
        }


        System.out.println("__________________");
        for (int v = 0; v < targets.length; v++) {
            System.out.println();
            System.out.println("variant: " + variants.get(v).index);
            for (int t = 0; t < targets[v].length; t++) {
                System.out.println("  target: " + targets[v][t].index);
                if (willBeLessUnderAttack(variants.get(v), targets[v][t])) {
                    points[v][t] += 4;
                    System.out.println("   willBeLessUnderAttack");
                }
                if (willBeMoreUnderAttack(variants.get(v), targets[v][t])) {
                    points[v][t] += -4;
                    System.out.println("   willBeMoreUnderAttack");
                }
                if (willBeUnderAtackAfterStep(variants.get(v), targets[v][t])) {
                    System.out.println("   willBeUnderAtackAfterStep");
                    if (act.isMove()) {
                        points[v][t] += -1;
                    }
                    if (act.isFight() && willBeFighterAfter(variants.get(v), targets[v][t])) {
                        points[v][t] += 1;
                        System.out.println("   willBeFighterAfter AND FIGHT");
                        points[v][t] += commonCheckPoints(variants.get(v), targets[v][t], maxIndex, minIndex);
                    }
                } else {
                    if (willBeFighterAfter(variants.get(v), targets[v][t])) {
                        points[v][t] += 3;
                        System.out.println("   willBeFighterAfter");
                        points[v][t] += commonCheckPoints(variants.get(v), targets[v][t], maxIndex, minIndex);

                    } else {
                        points[v][t] += 1;
                        System.out.println("   NOT willBeFighterAfter");
                        points[v][t] += commonCheckPoints(variants.get(v), targets[v][t], maxIndex, minIndex);
                    }
                }
                System.out.println("   POINTS: " + points[v][t]);
            }
        }



        int min = -100;
        for (int v = 0; v < points.length; v++) {
            for (int t = 0; t < points[v].length; t++) {
                if (points[v][t] > min) {
                    min = points[v][t];
                }
            }
        }

        int maxValueCount = 0;
        for (int v = 0; v < points.length; v++) {
            for (int t = 0; t < points[v].length; t++) {
                if (points[v][t] == min) {
                    maxValueCount++;
                }
            }
        }

        int i = 0;
        Cell arrMaxVal[][] = new Cell[maxValueCount][];
        for (int v = 0; v < points.length; v++) {
            for (int t = 0; t < points[v].length; t++) {
                if (points[v][t] == min) {
                    arrMaxVal[i] = new Cell[]{variants.get(v), targets[v][t]};
                    i++;
                }
            }
        }

        if (maxValueCount > 0) {
            Random rand = new Random();
            return arrMaxVal[rand.nextInt(maxValueCount)];
        } else {
            return new Cell[]{new Cell(), new Cell()};
        }
    }

    private int commonCheckPoints(Cell variant, Cell target, int maxIndex, int minIndex) {  // определение веса хода
        int pointSummary = 0;
        if (isQueenIndex(target)) {
            pointSummary += 2;
            System.out.println("   isQueenIndex");
        }
        if (isCentralField(target)) {
            pointSummary += 1;
            System.out.println("   isCentralField");
        }
        if (variant.isWhiteChecker() && Integer.parseInt(target.index.substring(1)) == minIndex) {
            pointSummary += 1;
            System.out.println("min index: " + target.index + " " + minIndex);
        }
        if (variant.isBlackChecker() && Integer.parseInt(target.index.substring(1)) == maxIndex) {
            pointSummary += 1;
            System.out.println("max index: " + target.index + " " + maxIndex);
        }
        System.out.println("   Points in common step: " + pointSummary);
        return pointSummary;
    }

    private boolean willBeFighterAfter(Cell activeCell, Cell targetCell) throws NoSuchDirectionException {
        // прогноз того, какая шашка станет бойцом после шага
        ChessBoardData xData = new ChessBoardData();
        Cell activeCellTmp = new Cell();
        Cell targetCellTmp = new Cell();
        for (int i = 0; i < xData.cells.length; i++) {
            xData.cells[i].setStatus(data.cells[i].getStatus());
            if (activeCell.index.equals(xData.cells[i].index)) {
                activeCellTmp = xData.cells[i];
            }
            if (targetCell.index.equals(xData.cells[i].index)) {
                targetCellTmp = xData.cells[i];
            }
        }

        Direction d = getDbyTarget(xData, activeCellTmp, targetCellTmp);
        Cell victimCell = getVictim(xData, activeCellTmp, targetCellTmp, d);
        targetCellTmp.setStatus(activeCellTmp.getStatus());
        activeCellTmp.setBlackCell();
        if (victimCell.isExist()) {
            victimCell.setTurkichChecker();
        }

        return isSome(xData, targetCellTmp, FIGHT);
    }

    private boolean willBeLessUnderAttack(Cell activeCell, Cell targetCell) throws NoSuchDirectionException {
        // прогноз вероятности быть под угрозой
        ChessBoardData xData = new ChessBoardData();
        Cell activeCellTmp = new Cell();
        Cell targetCellTmp = new Cell();
        for (int i = 0; i < xData.cells.length; i++) {
            //
            xData.cells[i].setStatus(data.cells[i].getStatus());
            if (activeCell.index.equals(xData.cells[i].index)) {
                activeCellTmp = xData.cells[i];
            }
            if (targetCell.index.equals(xData.cells[i].index)) {
                targetCellTmp = xData.cells[i];
            }
        }
        Player computer = activeCellTmp.isWhite() ? WHITE : BLACK;
        int ownUnderAttackBefore = 0;
        int ownUnderAttackAfter = 0;
        for (int i = 0; i < xData.cells.length; i++) {
            if (xData.cells[i].isOwn(activeCellTmp)) {
                if (isCellUnderAtack(xData, xData.cells[i], computer)) {
                    ownUnderAttackBefore++;
                }
            }
        }

        targetCellTmp.setStatus(activeCellTmp.getStatus());
        activeCellTmp.setBlackCell();
        for (int i = 0; i < xData.cells.length; i++) {
            if (xData.cells[i].isOwn(targetCellTmp)) {
                if (isCellUnderAtack(xData, xData.cells[i], computer)) {
                    ownUnderAttackAfter++;
                }
            }
        }
        return (ownUnderAttackAfter < ownUnderAttackBefore);
    }

    private boolean willBeMoreUnderAttack(Cell activeCell, Cell targetCell) throws NoSuchDirectionException {
        ChessBoardData xData = new ChessBoardData();
        Cell activeCellTmp = new Cell();
        Cell targetCellTmp = new Cell();
        for (int i = 0; i < xData.cells.length; i++) {
            //
            xData.cells[i].setStatus(data.cells[i].getStatus());
            if (activeCell.index.equals(xData.cells[i].index)) {
                activeCellTmp = xData.cells[i];
            }
            if (targetCell.index.equals(xData.cells[i].index)) {
                targetCellTmp = xData.cells[i];
            }
        }
        Player computer = activeCellTmp.isWhite() ? WHITE : BLACK;
        int ownUnderAttackBefore = 0;
        int ownUnderAttackAfter = 0;
        for (int i = 0; i < xData.cells.length; i++) {
            if (xData.cells[i].isOwn(activeCellTmp)) {
                if (isCellUnderAtack(xData, xData.cells[i], computer)) {
                    ownUnderAttackBefore++;
                }
            }
        }

        targetCellTmp.setStatus(activeCellTmp.getStatus());
        activeCellTmp.setBlackCell();
        for (int i = 0; i < xData.cells.length; i++) {
            if (xData.cells[i].isOwn(targetCellTmp)) {
                if (isCellUnderAtack(xData, xData.cells[i], computer)) {
                    ownUnderAttackAfter++;
                }
            }
        }
        return (ownUnderAttackAfter > ownUnderAttackBefore);
    }

    private boolean willBeUnderAtackAfterStep(Cell activeCell, Cell targetCell) throws NoSuchDirectionException {
//    |o| | |  Будет ли шашка под атакой ПОСЛЕ ХОДА на ближней дистанции(одна клетка) Рассматриваем вариант по диагонали:
// наша шашка, пустая клетка(куда собираемся походить), вражеская шашка.
//    | |x| |  После хода клетка, занятая нашей шашкой освободиться, и наша шашка станет под атаку. Чтобы этого избежать
// проверяем эту ситуацию.
//    | | |*|  Для обычных шашек проверяются ихние направления - черные - вниз, белые - вверх. Дамки проверяются по всем
// направлениям.
       for (Direction d : Direction.values()) {
            if (getCellByD(this.data, targetCell, d, 1).isOpposite(activeCell) && getCellByD(this.data, targetCell, getOppositeD(d), 1).equals(activeCell)) {
                return true;
            }
        }
// Проверяем накрест по диагонали наличие шашки противника, нашей шашки, пустой клетки.
        for (Direction d : Direction.values()) {
            if (getCellByD(this.data, targetCell, d, 1).isOpposite(activeCell) && getCellByD(this.data, targetCell, getOppositeD(d), 1).isBlackCell()) {
                return true;
            }
        }
// Ишем дамку во всех направлениях до конца диагонали или наличия шашки на диагонали, если находим, проверяем будет ли
// позади пустая клетка, или та с которой мы будем ходить (соответсвенно она будет тоже пустая после хода)
        for (Direction d : Direction.values()) {
            int i = 1;
            while (getCellByD(this.data, targetCell, d, i).isBlackCell() || (getCellByD(this.data, targetCell, d, i).isOwn(activeCell) && getCellByD(this.data, targetCell, d, i).equals(activeCell))) {
                i++;
            }
            if (getCellByD(this.data, targetCell, d, i).isOpposite(activeCell) && getCellByD(this.data, targetCell, d, i).isQueen()) {
                if (getCellByD(this.data, targetCell, getOppositeD(d), 1).isBlackCell() || getCellByD(this.data, targetCell, getOppositeD(d), 1).equals(activeCell)) {
                    return true;
                }
            }

        }
        return false;
    }

    //   |*| |*|   |x| |x| Проверяем по диагоналям под атакой ли любая шашка(клетка) НА ДАННЫЙ МОМЕНТ.
//   | |o| |   | |o| |
//   |x| |x|   |*| |*|
    private boolean isCellUnderAtack(ChessBoardData xData, Cell cell, Player player) throws NoSuchDirectionException {
        Cell tmpCell = new Cell();
        if (player.isWhite()) {
            tmpCell.setWhiteChecker();
        } else {
            tmpCell.setBlackChecker();
        }
// ближайшая вражеская шашка
        for (Direction d : Direction.values()) {
            if (getCellByD(xData, cell, d, 1).isOpposite(tmpCell) && getCellByD(xData, cell, getOppositeD(d), 1).isBlackCell()) {
                return true;
            }
        }
// поиск вражеской королевы
        for (Direction d : Direction.values()) {
            int i = 1;
            while (getCellByD(xData, cell, d, i).isBlackCell()) {
                i++;
            }
            if (getCellByD(xData, cell, d, i).isOpposite(tmpCell) && getCellByD(xData, cell, d, i).isQueen()) {
                if (getCellByD(xData, cell, getOppositeD(d), 1).isBlackCell()) {
                    return true;
                }
            }
        }
        return false;
    }

    private Direction getOppositeD(Direction d) throws NoSuchDirectionException {
        if (d == LU) {
            return RB;
        }
        if (d == RU) {
            return LB;
        }
        if (d == RB) {
            return LU;
        }
        if (d == LB) {
            return RU;
        }
        throw new NoSuchDirectionException();
    }

    private void resetTurkishArr() {
        for (int i = 0; i < turkishArr.size(); i++) {
            turkishArr.get(i).setBlackCell();
        }
        turkishArr.clear();
    }

    private boolean isSomeByD(ChessBoardData xData, Cell cell, Action act, Direction d) {
        int i = 1;
        while (getCellByD(xData, cell, d, i).isExist()) {
            if (isRightActByD(xData, cell, getCellByD(xData, cell, d, i), d, act)) {
                return true;
            }
            i++;
        }
        return false;
    }

    private boolean isSome(ChessBoardData xData, Cell cell, Action act) {
        for (Direction d : Direction.values()) {
            if (isSomeByD(xData, cell, act, d)) {
                return true;
            }

        }
        return false;
    }

    Cell getSome(ChessBoardData xData, Action act, Player pl) {
        for (int i = 0; i < xData.cells.length; i++) {
            if (xData.cells[i].isWhite() && isSome(xData, xData.cells[i], act) && pl.isWhite()) {
                return xData.cells[i];
            }
            if (xData.cells[i].isBlack() && isSome(xData, xData.cells[i], act) && pl.isBlack()) {
                return xData.cells[i];
            }
        }
        return new Cell();
    }

    //  return:
//  двидение    - [targetCell, new Cell()]
//  битва   - [targetCell, victimCell]
//  ничего - [new Cell(), new Cell()]
    private Cell[] checkCells(ChessBoardData xData, Cell activeCell, Cell targetCell, Direction d, Action act) {
        // проверка того, какое будет движение в следующую точку
        Cell victimCell;
        //движение
        if (act.isMove()) {
            //проверка правельного направления шашки
            if (((activeCell.isBlackChecker() || activeCell.isBlackActiveChecker()) && !d.isBlackD())
                    || ((activeCell.isWhiteChecker() || activeCell.isWhiteActiveChecker()) && !d.isWhiteD())) {
                return new Cell[]{new Cell(), new Cell()};
            }

            // шаг шашки
            if (activeCell.isChecker()) {
                if (getCellByD(xData, activeCell, d, 1).isBlackCell()) {
                    if (getCellByD(xData, activeCell, d, 1).equals(targetCell)) {
                        return new Cell[]{targetCell, new Cell()};
                    }
                }
            }
            // шаг королевы
            if (activeCell.isQueen()) {
                int deep = 1;
                while (getCellByD(xData, activeCell, d, deep).isBlackCell()) {
                    if (getCellByD(xData, activeCell, d, deep).equals(targetCell)) {
                        return new Cell[]{targetCell, new Cell()};
                    }
                    deep++;
                }
            }
        }
        //битва
        if (act.isFight()) {
            //шашка
            if (activeCell.isChecker()) {
                if (getCellByD(xData, activeCell, d, 1).isOpposite(activeCell)) {
                    if (getCellByD(xData, activeCell, d, 2).isBlackCell() && getCellByD(xData, activeCell, d, 2).equals(targetCell)) {
                        return new Cell[]{targetCell, getCellByD(xData, activeCell, d, 1)};
                    }
                }
            }
            //королева
            if (activeCell.isQueen()) {
                int deep = 1;
                while (getCellByD(xData, activeCell, d, deep).isBlackCell()) {
                    deep++;
                }
                victimCell = getCellByD(xData, activeCell, d, deep);
                if (victimCell.isOpposite(activeCell)) {
                    deep = 1;
                    while (getCellByD(xData, victimCell, d, deep).isBlackCell()) {
                        if (getCellByD(xData, victimCell, d, deep).equals(targetCell)) {
                            return new Cell[]{targetCell, victimCell};
                        }
                        deep++;
                    }
                }
            }
        }
        return new Cell[]{new Cell(), new Cell()};
    }

    private boolean isRightActByD(ChessBoardData xData, Cell activeCell, Cell targetCell, Direction d, Action act) {
        // выбор правельное направление
        Cell actCells[] = checkCells(xData, activeCell, targetCell, d, act);
        // движение
        if (act.isMove() && actCells[0].isExist() && (!actCells[1].isExist())) {
            return true;
        }
        // битва
        if (act.isFight() && actCells[0].isExist() && actCells[1].isExist()) {
            return true;
        }
        return false;
    }

    public Direction getDbyTarget(ChessBoardData xData, Cell activeCell, Cell targetCell) throws NoSuchDirectionException {    // задает направление с целью атаковать
        for (Direction d : Direction.values()) {
            int i = 1;
            while (getCellByD(xData, activeCell, d, i).isExist()) {
                if (targetCell.equals(getCellByD(xData, activeCell, d, i))) {
                    return d;
                }
                i++;
            }
//            System.out.println("active index, cx, cy" + "\n" + activeCell.index + "  " + activeCell.cX + "  " + activeCell.cY);
//            System.out.println("target index, cx, cy" + "\n" + targetCell.index + "  " + targetCell.cX + "  " + targetCell.cY);
        }
        throw new NoSuchDirectionException();
    }

    Cell getCellByXY(ChessBoardData xData, int clickedX, int clickedY) {
        for (Cell cell : xData.cells) {
            if ((clickedX >= (cell.cX))
                    && (clickedX < (cell.cX + xData.CELL_SIZE))
                    && (clickedY >= (cell.cY))
                    && (clickedY < (cell.cY + xData.CELL_SIZE))) {
                return cell;
            }
        }
        return new Cell();
    }

    public Cell getCellByXY(int clickedX, int clickedY) {
        for (Cell cell : data.cells) {
            if ((clickedX >= (cell.cX))
                    && (clickedX < (cell.cX + data.CELL_SIZE))
                    && (clickedY >= (cell.cY))
                    && (clickedY < (cell.cY + data.CELL_SIZE))) {
                return cell;
            }
        }
        return new Cell();
    }

    private Cell getCellByD(ChessBoardData xData, Cell cell, Direction d, int deep) {
        return getCellByXY(xData, cell.cX + xData.CELL_SIZE * d.kX * deep, cell.cY + xData.CELL_SIZE * d.kY * deep);
    }

    private boolean checkSetQeen(Cell cell) {   // устанавливает шашку как королеву
        String userIndexQ[] = {"a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8"};
        String compIndexQ[] = {"a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"};
        if (cell.isWhiteChecker()) {
            for (String uIndex : userIndexQ) {
                if (uIndex.equals(cell.index)) {
                    cell.setWhiteQueen();
                    return true;
                }
            }
        }
        if (cell.isBlackChecker()) {
            for (String cIndex : compIndexQ) {
                if (cIndex.equals(cell.index)) {
                    cell.setBlackQueen();
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isQueenIndex(Cell cell) {       // проверка находиться ли шашка на клетке превращающей в королеву
        String whiteIndexQueen[] = {"a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8"};
        String blackIndexQueen[] = {"a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"};
        if (cell.isBlackChecker()) {
            for (String bIndex : blackIndexQueen) {
                if (bIndex.equals(cell.index)) {
                    return true;
                }
            }
        }
        if (cell.isWhiteChecker()) {
            for (String wIndex : whiteIndexQueen) {
                if (wIndex.equals(cell.index)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isCentralField(Cell cell) { // положение шашки в центре
        String centralFields[] = {"b2", "b4", "b6", "c3", "c5", "c7", "d2", "d4", "d6", "e3", "e3", "e7", "f2", "f4", "f6", "g3", "g5", "g7"};

        for (String cetralField : centralFields) {
            if (cetralField.equals(cell.index)) {
                return true;
            }
        }
        return false;
    }

    void customResult() {       //тут определяются варианты окончания игры, убиты или блокированы
        data.setCheckersNum();
        data.notifyUpdateGUI();   //закоменченно из-за теста, без этого обновления в окошко текста
// не будут выводиться ходы игроков

        if (data.blackCheckers == 0) {
            data.gameOver = true;
            data.notifyNoBlackCkeckersLeft();
            return;
        }
        if (data.whiteCheckers == 0) {
            data.gameOver = true;
            data.notifyNoWhiteCkeckersLeft();
            return;
        }

        if (!getSome(this.data, Action.FIGHT, Player.BLACK).isExist() && !getSome(this.data, Action.MOVE, Player.BLACK).isExist() && data.blackCheckers != 0) {
            data.gameOver = true;
            data.notifyBlackIsBlocked();
            return;
        }
        if (!getSome(this.data, Action.FIGHT, Player.WHITE).isExist() && !getSome(this.data, Action.MOVE, Player.WHITE).isExist() && data.whiteCheckers != 0) {
            data.gameOver = true;
            data.notifyWhiteIsBlocked();
            return;
        }
        data.resultBuf = "";
    }

    Cell getCellByIndex(String index) {
        for (int i = 0; i < data.cells.length; i++) {
            if (index.equals(data.cells[i].index)) {
                return data.cells[i];
            }
        }
        return new Cell();
    }

    public Logic() {
        this.data = new ChessBoardData();
        (new Thread(new ObservePlayerQueue(this))).start();
    }
}