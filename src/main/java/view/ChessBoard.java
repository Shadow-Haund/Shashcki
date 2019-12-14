package view;

import model.Cell;
import model.ChessBoardData;

import javax.swing.*;
import java.awt.*;

class ChessBoard extends JPanel {

    ChessBoardData data;

    ChessBoard(ChessBoardData data) {
        this.data = data;
        this.setMinimumSize(data.PREFERRED_SIZE);
        this.setPreferredSize(data.PREFERRED_SIZE);
    }// конструктор

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        Font font = new Font("Dialog", Font.PLAIN, 14);
        g2d.setFont(font);

        // Отрисовка букв и цифр у доски
        for (int i = 1; i < (data.CELL_SIDE_NUM + 1); i++) {
            // цифры
            g2d.drawString(Integer.toString(data.REVERS_NUMBERS[i]), data.OFFSET_LEFT_BOUND + 40, data.OFFSET_TOP_BOUND + (i * data.CELL_SIZE) + 30);
            // буквы
            g2d.drawString(data.LITERALS[i], data.OFFSET_LEFT_BOUND + (i * data.CELL_SIZE) + 20, data.OFFSET_TOP_BOUND + 50);
        }

        for (int cCount = 0; cCount < data.CELL_NUM; cCount++) {
            Cell cell = data.cells[cCount];
            data.checkerX = cell.cX + data.CELL_SIZE / 2 - data.CHECKER_DIAMETER / 2; //без них шашки съезжают в левый край
            data.checkerY = cell.cY + data.CELL_SIZE / 2 - data.CHECKER_DIAMETER / 2; //верхний край

            switch (cell.getStatus()) { // отрисовка согласно статусу
                case WC:
                    paintCell(Color.WHITE, g2d, cell);
                    break;
                case BC:
                    paintCell(Color.GRAY, g2d, cell);
                    break;
                case WHITE_CH:
                    paintChecker(Color.WHITE, g2d, cell);
                    break;
                case BLACK_CH:
                    paintChecker(Color.BLACK, g2d, cell);
                    break;
                case WHITE_ACH:
                    paintChecker(Color.RED, g2d, cell);
                    break;
                case BLACK_ACH:
                    paintChecker(Color.BLUE, g2d, cell);
                    break;
                case WHITE_Q:
                    paintQueen(Color.WHITE, Color.LIGHT_GRAY, g2d, cell);
                    break;
                case BLACK_Q:
                    paintQueen(Color.BLACK, Color.LIGHT_GRAY, g2d, cell);
                    break;
                case WHITE_AQ:
                    paintQueen(Color.WHITE, Color.RED, g2d, cell);
                    break;
                case BLACK_AQ:
                    paintQueen(Color.BLACK, Color.RED, g2d, cell);
                    break;
                case TBCH:
                    paintChecker(Color.GREEN, g2d, cell);
                    break;
            }

        }// конец цикла
        this.setPreferredSize(data.PREFERRED_SIZE);
        repaint();
    }

    private void paintCell(Color color, Graphics2D g2d, Cell cell) {
        g2d.setPaint(color);
        g2d.fillRect(cell.cX, cell.cY, data.CELL_SIZE, data.CELL_SIZE);
    }

    private void paintChecker(Color color, Graphics2D g2d, Cell cell) {
        g2d.setPaint(Color.GRAY);
        g2d.fillRect(cell.cX, cell.cY, data.CELL_SIZE, data.CELL_SIZE);
        g2d.setPaint(color);
        g2d.fillOval(data.checkerX, data.checkerY, data.CHECKER_DIAMETER, data.CHECKER_DIAMETER);
    }

    private void paintQueen(Color color, Color colorInner, Graphics2D g2d, Cell cell) {
        g2d.setPaint(Color.GRAY);
        g2d.fillRect(cell.cX, cell.cY, data.CELL_SIZE, data.CELL_SIZE);
        g2d.setPaint(color);
        g2d.fillOval(data.checkerX, data.checkerY, data.CHECKER_DIAMETER, data.CHECKER_DIAMETER);
        g2d.setPaint(colorInner);
        g2d.fillOval(data.checkerX + data.QUEEN_INNER_OFFSET, data.checkerY + data.QUEEN_INNER_OFFSET, data.QUEEN_INNER_DIAMETER, data.QUEEN_INNER_DIAMETER);
    }
}
