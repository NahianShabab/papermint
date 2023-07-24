class GrammerException extends Exception{

}
enum Token{
    E,T,F,E_BAR,T_BAR,NUM,RIGHT_PAR,LEFT_PAR,ADDOP,MULOP,INT,DOUBLE,
    P,STMNTS,STMNT,ID,ASSIGNMENT,VARIABLE,DECLARATION,NEWLINE,EQUAL
}
public class Parser{
    Token tokens[]={
        Token.NUM,Token.MULOP,Token.NUM,Token.NEWLINE
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
        if(next_token==Token.INT || next_token==Token.DOUBLE){
            declaration();
            match_terminal(Token.NEWLINE);
        }
        else if(next_token==Token.NUM || next_token==Token.LEFT_PAR){
            e();
            match_terminal(Token.NEWLINE);
        }else{
            int current_token_pos=token_pos;
            try{
                assignment();
                match_terminal(Token.NEWLINE);
            }catch(GrammerException ge){
                token_pos=current_token_pos;
                e();
                match_terminal(Token.NEWLINE);
            }
        }
    }
    void assignment() throws GrammerException{
        int current_token_pos=token_pos;
        try{
            variable();
            match_terminal(Token.EQUAL);
            assignment();
        }catch(GrammerException ge){
            token_pos=current_token_pos;
            variable();
            match_terminal(Token.EQUAL);
            e();
        }
    }
    void declaration() throws GrammerException{
        print_grammer("Declaration", "Type ID");
        type();
        match_terminal(Token.ID);
    }
    void type() throws GrammerException{
        Token next_token=peekNextToken();
        if(next_token==Token.INT){
            print_grammer("Type", "INT");
            match_terminal(Token.INT);
        }else{
            print_grammer("Type", "DOUBLE");
            match_terminal(Token.DOUBLE);
        }
    }
    void e() throws GrammerException{
        print_grammer("E", "TE\'");
        t();
        e_bar();
    }
    void e_bar()throws GrammerException{
        Token next_token=peekNextToken();
        if(next_token==Token.NEWLINE || next_token==Token.RIGHT_PAR){
            print_grammer("E\'", "empty");
        }else{
            print_grammer("E\'", "+TE\'");
            match_terminal(Token.ADDOP);
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
        if(next_token==Token.ADDOP || next_token==Token.RIGHT_PAR || next_token==Token.NEWLINE){
            print_grammer("T\'", "empty");
        }
        else{
            print_grammer("T\'", "*FT\'");
            match_terminal(Token.MULOP);
            f();
            t_bar();
        }
    }
    void f() throws GrammerException{
        if (peekNextToken()==Token.NUM){
            print_grammer("F", "NUMBER");
            match_terminal(Token.NUM);
        }else if(peekNextToken()==Token.LEFT_PAR){
            print_grammer("F", "(E)");
            match_terminal(Token.LEFT_PAR);
            e();
            match_terminal(Token.RIGHT_PAR);
        }
        else{
            print_grammer("F", "Variable");
            variable();
        }
    }
    void variable() throws GrammerException{
        match_terminal(Token.ID);
    }
    void match_terminal(Token expected_token)throws GrammerException{
        if (expected_token!=getNextToken())
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