package model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;

import static model.ChessBoardData.GameActors.*;
import static model.ChessBoardData.Status.*;

public class ChessBoardData {

    public boolean whiteIsHuman = true;
    public boolean blackIsHuman = true;
    public boolean whiteSessionContinue = true;
    public boolean blackSessionContinue = false;
    public String resultBuf;
    // отображение числа шашек на доске
    public int whiteCheckers = 12;
    public int blackCheckers = 12;
    // размер стороны клетки доски
    public final int CELL_SIZE = 55;
    // кол-во клеток
    public final int CELL_SIDE_NUM = 8;
    public final int CELL_NUM = CELL_SIDE_NUM * CELL_SIDE_NUM;
    public Cell cells[] = new Cell[CELL_NUM];
    // диаметр шашки
    public int checkerX;
    public int checkerY;
    // растояние от границ окна
    public final int OFFSET_LEFT_BOUND = -30;
    public final int OFFSET_TOP_BOUND = -30;
    // диаметр королевы
    public final int CHECKER_DIAMETER = 50;
    public final int QUEEN_INNER_DIAMETER = 40;
    public final int QUEEN_INNER_OFFSET = (CHECKER_DIAMETER - QUEEN_INNER_DIAMETER) / 2;
    public final Dimension PREFERRED_SIZE = new Dimension(CELL_SIDE_NUM * CELL_SIZE + CELL_SIZE, CELL_SIDE_NUM * CELL_SIZE + CELL_SIZE);
    // массивы для makeIndex() функции и отрисовка значений клеток доски
    public final String LITERALS[] = {"NULL", "a", "b", "c", "d", "e", "f", "g", "h"};//
    public final int REVERS_NUMBERS[] = {0, 8, 7, 6, 5, 4, 3, 2, 1};
    // переменные для текста
    public String stepWhiteText;
    public String stepBlackText;
    public String userHasFighterText;
    public String userMustFightText;
    public String wrongNextCellText;
    public String frameTitle;
    public String gameTitle;
    public String settingsTitle;
    public String languageTitle;
    public String gameActorsTitle;
    public String compVSuserTitle;
    public String userVScompTitle;
    public String userVSuserTitle;
    public String compVScompTitle;
    public String helpTitle;
    public String newGameTitle;
    public String exitTitle;
    public String rulesTitle;
    public String rulesLink;
    public String aboutTitle;
    public String labelBlackTitle;
    public String labelWhiteTitle;
    public String noWhiteCheckersText;
    public String noBlackCheckersText;
    public String whiteIsBlockedText;
    public String blackIsBlockedText;
    public String whiteWon;
    public String blackWon;
    public String dialogNewGame;
    public String dialogExit;
    // координаты шашек от левого верхнего угла
    int cX;
    int cY;
    boolean gameExit = false;
    boolean gameOver = false;
    // переменная языка
    Lang LANG = Lang.RU;
    private NodeList menuValues;
    private DataListener dataListener;

    public enum Status {

        NILL, // нулевое состояние
        WC, // белая клетка
        BC, // черная клетка
        WHITE_CH, // белая шашка
        BLACK_CH, // черная шашка
        WHITE_Q, // белая королева
        BLACK_Q, // черная королева
        WHITE_ACH, // белая активная шашка
        BLACK_ACH, // черная активная шашка
        WHITE_AQ, // белая активная королева
        BLACK_AQ, // черная активная королева
        TBCH; // турецкая шашка (взятие двух и более шашек за один ход)
    }

    enum Lang {

        RU, ENG;
    }

    enum GameActors {

        USERvsCOMP, // игрок против компьютера
        COMPvsUSER, // компьютер против игрока
        USERvsUSER, // игрок против игрока
        COMPvsCOMP // компьютер против компьютера
    }

    public void setUSERvsCOMP() {
        setGameActors(USERvsCOMP);
    }

    public void setCOMPvsUSER() { setGameActors(COMPvsUSER); }

    public void setCOMPvsCOMP() {
        setGameActors(COMPvsCOMP);
    }

    public void setUSERvsUSER() {
        setGameActors(USERvsUSER);
    }

    void setGameActors(GameActors GA) {
        if (GA == USERvsCOMP) {
            whiteIsHuman = true;
            blackIsHuman = false;
        }
        if (GA == COMPvsUSER) {
            whiteIsHuman = false;
            blackIsHuman = true;
        }
        if (GA == COMPvsCOMP) {
            whiteIsHuman = false;
            blackIsHuman = false;
        }
        if (GA == USERvsUSER) {
            whiteIsHuman = true;
            blackIsHuman = true;
        }
    }

    public void restartGame() {
        gameOver = false;
        resetData();
        initCellsArr();
    }

    public void setGameExit() {
        gameExit = true;
    }

    void resetData() {
        gameOver = true;
        gameOver = false;
        whiteCheckers = 12;
        blackCheckers = 12;
        whiteSessionContinue = true;
        blackSessionContinue = false;
    }

    public void setEnglishLang() {
        setLanguage(Lang.ENG);
    }

    public void setRussianLang() { setLanguage(Lang.RU); }


    private void setLanguage(Lang lang) {
        initMenuValbyXML(lang);
        LANG = lang;
        stepWhiteText = getFromXML("stepWhiteText");
        stepBlackText = getFromXML("stepBlackText");
        userHasFighterText = getFromXML("userHasFighterText");
        userMustFightText = getFromXML("userMustFightText");
        wrongNextCellText = getFromXML("wrongNextCellText");
        frameTitle = getFromXML("frameTitle");
        gameTitle = getFromXML("gameTitle");
        settingsTitle = getFromXML("settingsTitle");
        gameActorsTitle = getFromXML("gameActorsTitle");
        userVScompTitle = getFromXML("userVScompTitle");
        compVSuserTitle = getFromXML("compVSuserTitle");
        userVSuserTitle = getFromXML("userVSuserTitle");
        compVScompTitle = getFromXML("compVScompTitle");
        languageTitle = getFromXML("languageTitle");
        helpTitle = getFromXML("helpTitle");
        newGameTitle = getFromXML("newGameTitle");
        exitTitle = getFromXML("exitTitle");
        rulesTitle = getFromXML("rulesTitle");
        rulesLink = getFromXML("rulesLink");
        aboutTitle = getFromXML("aboutTitle");
        labelBlackTitle = getFromXML("labelBlackTitle");
        labelWhiteTitle = getFromXML("labelWhiteTitle");
        noWhiteCheckersText = getFromXML("noWhiteCheckersText");
        noBlackCheckersText = getFromXML("noBlackCheckersText");
        whiteIsBlockedText = getFromXML("whiteIsBlockedText");
        blackIsBlockedText = getFromXML("blackIsBlockedText");
        whiteWon = getFromXML("whiteWon");
        blackWon = getFromXML("blackWon");
        dialogNewGame = getFromXML("dialogNewGame");
        dialogExit = getFromXML("dialogExit");
        if (dataListener != null) {
            dataListener.updateTextGuiLanguageInfo(new UpdateGuiEvent(this));
        }
    }

    private void initMenuValbyXML(Lang lang) {  // парсит XML файл
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse("./menu.xml");
            Element rootElementLang = document.getDocumentElement();
            NodeList langElements = rootElementLang.getChildNodes();
            for (int i = 0; i < langElements.getLength(); i++) {
                if (langElements.item(i).getNodeName().equals(lang.toString())) {
                    menuValues = langElements.item(i).getChildNodes();
                }
            }
        } catch (Exception exception) {
            System.out.println("XML parsing error!");
            exception.printStackTrace();
        }
    }

    private String getFromXML(String elementName) {     // взятие данных из файла
        for (int i = 0; i < menuValues.getLength(); i++) {
            if (menuValues.item(i).getNodeName().equals(elementName)) {
                return menuValues.item(i).getTextContent();
            }
        }
        return "Some problem in XML language file";
    }

    void setCheckersNum() {     // устанавливает кол-во шашек
        int compNum = 0;
        int userNum = 0;
        for (int i = 0; i < cells.length; i++) {
            if (cells[i].isBlack()) {
                compNum++;
            }
            if (cells[i].isWhite()) {
                userNum++;
            }
        }
        blackCheckers = compNum;
        whiteCheckers = userNum;
    }

    /**
     *
     */
    private String makeIndex(int indexLiteralX, int indexDigitY) {      // превращение в index значений клеток
        return LITERALS[indexLiteralX] + (Integer.toString(REVERS_NUMBERS[indexDigitY]));
    }

    void notifyNoBlackCkeckersLeft() {
        dataListener.noBlackCkeckersLeft(new UpdateGuiEvent(this));
    }

    void notifyNoWhiteCkeckersLeft() {
        dataListener.noWhiteCkeckersLeft(new UpdateGuiEvent(this));
    }

    void notifyBlackIsBlocked() {
        dataListener.blackIsBlocked(new UpdateGuiEvent(this));
    }

    void notifyWhiteIsBlocked() {
        dataListener.whiteIsBlocked(new UpdateGuiEvent(this));
    }

    void notifyUpdateGUI() {
        dataListener.updateGUI(new UpdateGuiEvent(this));
    }

    private void initCellsArr() {   //расположение по цвету клеток шашек
        int cellCount = 0;
        // отрисовываем линии кдеток вертикально
        for (int vert = 1; vert < CELL_SIDE_NUM + 1; vert++) {
            // определяем непарные линии
            if (vert % 2 != 0) {
                //гоизонтально меняем черны и белые клетки и левый нижний угол праверяем условием
                for (int hor = 1; hor < (CELL_SIDE_NUM + 1); hor++) {
                    cX = OFFSET_LEFT_BOUND + (hor * CELL_SIZE);
                    cY = OFFSET_TOP_BOUND + (vert * CELL_SIZE);
                    // непарные столбцы в непарных строках белые
                    if (hor % 2 != 0) {
                        cells[cellCount] = new Cell(makeIndex(hor, vert), cX, cY, WC);
                        cellCount++;
                    }
                    // парные столбцы в парных строках черные
                    if (hor % 2 == 0) {
                        // на черных расологаются шашки, выше 5 строки белые ниже 4, черные
                        if (vert > (CELL_SIDE_NUM / 2 + 1)) {
                            cells[cellCount] = new Cell(makeIndex(hor, vert), cX, cY, WHITE_CH);
                            cellCount++;
                        } else if (vert < (CELL_SIDE_NUM / 2)) {
                            cells[cellCount] = new Cell(makeIndex(hor, vert), cX, cY, BLACK_CH);
                            cellCount++;
                        } else {
                            cells[cellCount] = new Cell(makeIndex(hor, vert), cX, cY, BC);
                            cellCount++;
                        }
                    }
                }
            }
            else { // отрисовываем линии кдеток горизонтально
                for (int hor = 1; hor < (CELL_SIDE_NUM + 1); hor++) {
                    cX = OFFSET_LEFT_BOUND + (hor * CELL_SIZE);
                    cY = OFFSET_TOP_BOUND + (vert * CELL_SIZE);
                    // непарные столбцы в парных сроках черные
                    if (hor % 2 != 0) {
                        // на черных расологаются шашки, выше 5 строки белые ниже 4, черные
                        if (vert > (CELL_SIDE_NUM / 2 + 1)) {
                            cells[cellCount] = new Cell(makeIndex(hor, vert), cX, cY, WHITE_CH);
                            cellCount++;
                        } else if (vert < (CELL_SIDE_NUM / 2)) {
                            cells[cellCount] = new Cell(makeIndex(hor, vert), cX, cY, BLACK_CH);
                            cellCount++;
                        } else {
                            cells[cellCount] = new Cell(makeIndex(hor, vert), cX, cY, BC);
                            cellCount++;
                        }
                    }
                    // парные столбцы в непарных сроках черные

                    if (hor % 2 == 0) {
                        cells[cellCount] = new Cell(makeIndex(hor, vert), cX, cY, WC);
                        cellCount++;
                    }
                }
            }
        }
    }

    public void addDataListener(DataListener dataListener) {
        this.dataListener = dataListener;
    }

    public ChessBoardData() {
        initCellsArr();
    }
}