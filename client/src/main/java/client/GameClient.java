package client;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import client.ws.WebSocketFacade;
import model.request.WsMoveMergeRequest;
import model.request.WsMoveRequest;
import ui.ChessBoardDrawer;
import websocket.commands.UserGameCommand;

import java.util.*;

import static java.util.Map.entry;

public class GameClient extends BaseClient{

    private String auth;
    private String userColor = "WHITE";
    private Integer gameID;
    private ChessBoard currentBoard;
    private ChessBoardDrawer chessBoardDrawer;
    private WebSocketFacade webSocketFacade;
    private Map<String, Integer> cToInt = Map.ofEntries(
            entry("a", 1),
            entry("b", 2),
            entry("c", 3),
            entry("d", 4),
            entry("e", 5),
            entry("f", 6),
            entry("g", 7),
            entry("h", 8)
    );

    public GameClient(String auth) {
        this.auth = auth;
        currentBoard = new ChessBoard();
        currentBoard.resetBoard();
        chessBoardDrawer = new ChessBoardDrawer();
    }

    @Override
    protected boolean shouldExit(String result) {
        return result.equals("leave");
    }

    public void run(WebSocketFacade webSocketFacade) {
        this.webSocketFacade = webSocketFacade;
        run();
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = tokens.length > 0 ? tokens[0] : "help";
            var args = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "help" -> help();
                case "r" -> redraw();
                case "l" -> leave();
                case "m" -> makeMove(args);
                case "re" -> resign(args);
                case "h" -> highlight(args);
                default -> help();
            };
        }catch (Exception e){
            return e.getMessage();
        }
    }

    public String redraw() {
        ChessBoardDrawer.drawChessBoard(webSocketFacade.getChessGame().getBoard(), userColor, null);
        return "Draw board";
    }

    public String leave() {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, auth, gameID);
        this.webSocketFacade.runUserCommand(new Gson().toJson(command));
        return "leave";
    }

    public String makeMove(String... params) {
        ChessPiece.PieceType promoPiece = null;
        if (params.length == 3) {
            promoPiece = ChessPiece.PieceType.valueOf(params[2]);
        }else if (params.length != 2) {
            return "Error";
        }
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, auth, gameID);
            Integer cTo1 = cToInt.get(String.valueOf(params[0].charAt(1)));
//            if (userColor.equals("white")) {
//                cTo1 = 9 - cTo1;
//            }
            ChessPosition startPosition = new ChessPosition(
                    Integer.parseInt(String.valueOf(params[0].charAt(0))), cTo1
            );
            Integer cTo2 = cToInt.get(String.valueOf(params[1].charAt(1)));
//            if (userColor.equals("white")) {
//                cTo2 = 9 - cTo2;
//            }
            ChessPosition endPosition = new ChessPosition(
                    Integer.parseInt(String.valueOf(params[1].charAt(0))), cTo2
            );
            WsMoveRequest wsMoveRequest = new WsMoveRequest(new ChessMove(startPosition, endPosition, promoPiece));
            WsMoveMergeRequest wsMoveMergeRequest = new WsMoveMergeRequest(command, wsMoveRequest);

            this.webSocketFacade.makeChessMove(new Gson().toJson(wsMoveMergeRequest));
        } catch (Exception e) {
            return "Error";
        }
        return "makeMove";
    }

    public String resign(String... params) {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, auth, gameID);
        Scanner in = new Scanner(System.in);
        String input = "";
        while(!input.equals("y") && !input.equals("n")){
            System.out.println("Are you sure you want to resign? y for yes & n for no: ");
            input = in.nextLine();
        }
        if (input.equals("y")){
            this.webSocketFacade.runUserCommand(new Gson().toJson(command));
            return "resign";
        }else{
            return "No resigned";
        }
    }

    public String highlight(String... params) {
        if (params.length != 1) {
            return "Error";
        }
        try {
            Integer cto1 = cToInt.get(String.valueOf(params[0].charAt(1)));
//            cto1 = 9 - cto1;
            ChessPosition startPosition = new ChessPosition(
                    Integer.parseInt(String.valueOf(params[0].charAt(0))), cto1
            );
            Collection<ChessMove> validMoves = webSocketFacade.getChessGame().validMoves(startPosition);
            ChessBoardDrawer.drawChessBoard(webSocketFacade.getChessGame().getBoard(), userColor, validMoves);
        } catch (Exception e){
            return "Error";
        }
        return "highlighted";
    }

    public String help() {
        return  """
                Options:
                - Show help command: "help"
                - Redraw Chess Board: "r"
                - Leave Chess Board: "l"
                - Make Move: "m start end promotion"
                - Resign Chess Board: "re"
                - Highlight legal moves: "h position"
                """;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public void setUserColor(String userColor) {
        this.userColor = userColor;
    }

    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }

    public Integer getGameID() {
        return gameID;
    }

    public void setCurrentBoard(ChessBoard currentBoard) {
        this.currentBoard = currentBoard;
    }
}
