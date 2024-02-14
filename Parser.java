class GrammerException extends Exception{

}
public class Parser{
    Token tokens[]={
        new Token(TokenType.NUM,"3"),
        new Token(TokenType.MULOP, "*"),
        new Token(TokenType.NUM, "4"),
        new Token(TokenType.NEWLINE, "\n"),
    };
    int token_pos=0;
    void print_grammer(String left,String right){
        System.out.println(left+" -> "+right);
    }
    Token getNextToken(){
        if (token_pos<tokens.length){
            return tokens[token_pos++];
        }
        return null;
    }
    Token peekNextToken(){
        if (token_pos<tokens.length){
            return tokens[token_pos];
        }
        return null;
    }
    void p() throws GrammerException{
        Token next_token=peekNextToken();
        if(next_token==null){
            print_grammer("P", "empty");
        }else{
            stmnts();
        }
    }
    void stmnts() throws GrammerException{
        Token next_token=peekNextToken();
        if(next_token==null){
            print_grammer("Stmnts", "empty");
        }else{
            print_grammer("Stmnts", "Stmnt Stmnts");
            stmnt();
            stmnts();
        }
    }
    void stmnt() throws GrammerException{
        Token next_token=peekNextToken();
        if(next_token.tokenType==TokenType.INT || next_token.tokenType==TokenType.DOUBLE){
            declaration();
            match_terminal(TokenType.NEWLINE);
        }
        else if(next_token.tokenType==TokenType.NUM || next_token.tokenType==TokenType.LEFT_PAR){
            e();
            match_terminal(TokenType.NEWLINE);
        }else{
            int current_token_pos=token_pos;
            try{
                assignment();
                match_terminal(TokenType.NEWLINE);
            }catch(GrammerException ge){
                token_pos=current_token_pos;
                e();
                match_terminal(TokenType.NEWLINE);
            }
        }
    }
    void assignment() throws GrammerException{
        int current_token_pos=token_pos;
        try{
            variable();
            match_terminal(TokenType.EQUAL);
            assignment();
        }catch(GrammerException ge){
            token_pos=current_token_pos;
            variable();
            match_terminal(TokenType.EQUAL);
            e();
        }
    }
    void declaration() throws GrammerException{
        print_grammer("Declaration", "Type ID");
        type();
        match_terminal(TokenType.ID);
    }
    void type() throws GrammerException{
        Token next_token=peekNextToken();
        if(next_token.tokenType==TokenType.INT){
            print_grammer("Type", "INT");
            match_terminal(TokenType.INT);
        }else{
            print_grammer("Type", "DOUBLE");
            match_terminal(TokenType.DOUBLE);
        }
    }
    void e() throws GrammerException{
        print_grammer("E", "TE\'");
        t();
        e_bar();
    }
    void e_bar()throws GrammerException{
        Token next_token=peekNextToken();
        if(next_token.tokenType==TokenType.NEWLINE || next_token.tokenType==TokenType.RIGHT_PAR){
            print_grammer("E\'", "empty");
        }else{
            print_grammer("E\'", "+TE\'");
            match_terminal(TokenType.ADDOP);
            t();
            e_bar();
        }
    }
    void t()throws GrammerException{
        print_grammer("T", "FT\'");
        f();
        t_bar();
    }
    void t_bar() throws GrammerException{
        Token next_token=peekNextToken();
        if(next_token.tokenType==TokenType.ADDOP || next_token.tokenType==TokenType.RIGHT_PAR || next_token.tokenType==TokenType.NEWLINE){
            print_grammer("T\'", "empty");
        }
        else{
            print_grammer("T\'", "*FT\'");
            match_terminal(TokenType.MULOP);
            f();
            t_bar();
        }
    }
    void f() throws GrammerException{
        if (peekNextToken().tokenType==TokenType.NUM){
            print_grammer("F", "NUMBER");
            match_terminal(TokenType.NUM);
        }else if(peekNextToken().tokenType==TokenType.LEFT_PAR){
            print_grammer("F", "(E)");
            match_terminal(TokenType.LEFT_PAR);
            e();
            match_terminal(TokenType.RIGHT_PAR);
        }
        else{
            print_grammer("F", "Variable");
            variable();
        }
    }
    void variable() throws GrammerException{
        match_terminal(TokenType.ID);
    }
    void match_terminal(TokenType expected_token)throws GrammerException{
        if (expected_token!=getNextToken().tokenType)
            throw new GrammerException();
        print_grammer(expected_token.toString(), expected_token.toString());
    }
    public void parse(){
        try{
            p();
        }catch(GrammerException g){
            System.out.println("parse error");
        }
    }
    public static void main(String[] args) {
        Parser parser=new Parser();
        parser.parse();
    }

}