package sk.tuke.gamestudio.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sk.tuke.gamestudio.game.checkers.core.*;

//http://localhost:8080
@Controller
@RequestMapping("/checkers")
public class CheckersController {
    private final CheckersField field = new CheckersField();

    @GetMapping
    public String checkers(@RequestParam(required = false) Integer fr, @RequestParam(required = false) Integer fc) {
        //field.move(fr, fc, 0, 0);
        return "checkers";
    }

    @GetMapping("/checkers/score")
    public String checkers(Model model) {
        model.addAttribute("whiteScore", field.getScoreWhite());
        model.addAttribute("blackScore", field.getScoreBlack());
        return "checkers";
    }

    @GetMapping("/new")
    public String newGame() {
        field.startNewGame();
        return "checkers";
    }

    public String getHtmlField() {
        StringBuilder sb = new StringBuilder();

        sb.append("<table class='checkers-field'>\n");
        for (int row = 0; row < 8; row++) {
            sb.append("<tr>\n");
            for (int col = 0; col < 8; col++) {
                Tile tile = field.getField()[row][col];
                String colorBackground = (row + col) % 2 == 0 ? "empty_white" : "empty_black";
                StringBuilder cell = new StringBuilder();

                cell.append(String.format(
                        "<td id='tile-%d-%d' class='tile' onclick='selectTile(%d, %d)'>" +
                                "<div class='background'>" +
                                "<img src='/images/%s.png' class='background' alt='background'>",
                        row, col, row, col, colorBackground));

                if (!tile.isEmpty()) {
                    String pieceImage = getImageName(tile);
                    cell.append(String.format(
                            "<img src='/images/%s.png' class='piece' id='piece-%d-%d' alt='%s'>",
                            pieceImage, row, col, pieceImage));
                }

                cell.append("</div></td>\n");

                sb.append(cell);
            }
            sb.append("</tr>\n");
        }
        sb.append("</table>\n");

        sb.append(String.format(
                "<script>updateScore(%d, %d);</script>",
                field.getScoreWhite(), field.getScoreBlack()));

        return sb.toString();
    }

    private String getImageName(Tile tile) {
        switch (tile.getState()) {
            case WHITE:
                return "white_checker";
            case BLACK:
                return "black_checker";
            case WHITE_KING:
                return "white_king";
            case BLACK_KING:
                return "black_king";
            case EMPTY_WHITE:
                return "empty_white";
            case EMPTY_BLACK:
                return "empty_black";
            default:
                return "";
        }
    }
}
