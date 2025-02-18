import chess.*;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);

        try {
            var port = 8000;

        }catch (Throwable e) {
            System.out.println("Unable to start server " + e.getMessage());
        }
        System.out.println("""
                Chess Server:
                java ServerMain <port> [sql]
                """);
    }
}