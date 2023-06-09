package es.ceu.gisi.modcomp.cyk_algorithm.algorithm;
import es.ceu.gisi.modcomp.cyk_algorithm.algorithm.exceptions.CYKAlgorithmException;
import es.ceu.gisi.modcomp.cyk_algorithm.algorithm.interfaces.CYKAlgorithmInterface;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Esta clase contiene la implementación de la interfaz CYKAlgorithmInterface
 * que establece los métodos necesarios para el correcto funcionamiento del
 * proyecto de programación de la asignatura Modelos de Computación.
 *
 * @author Sergio Saugar García <sergio.saugargarcia@ceu.es>
 */
public class CYKAlgorithm implements CYKAlgorithmInterface {
    ArrayList<Character> nonTerminals = new ArrayList<Character>();
    ArrayList<Character> Terminals = new ArrayList<Character>();
    char axioma;
    Map<Character,String> producciones= new HashMap<Character,String>();

    @Override
    /**
     * Método que añade los elementos no terminales de la gramática.
     *
     * @param nonterminal Por ejemplo, 'S'
     * @throws CYKAlgorithmException Si el elemento no es una letra mayúscula.
     */
    public void addNonTerminal(char nonterminal) throws CYKAlgorithmException {
        if (!Character.isUpperCase(nonterminal)|| nonTerminals.contains(nonterminal)) {
            throw new CYKAlgorithmException();
        }
        nonTerminals.add(nonterminal);
    }

    @Override
    /**
     * Método que añade los elementos terminales de la gramática.
     *
     * @param terminal Por ejemplo, 'a'
     * @throws CYKAlgorithmException Si el elemento no es una letra minúscula.
     */
    public void addTerminal(char terminal) throws CYKAlgorithmException {
        if(!Character.isLowerCase(terminal)|| Terminals.contains(terminal)){
            throw new CYKAlgorithmException();
        }else{
            Terminals.add(terminal);
        }
    }

    @Override
    /**
     * Método que indica, de los elementos no terminales, cuál es el axioma de
     * la gramática.
     *
     * @param nonterminal Por ejemplo, 'S'
     * @throws CYKAlgorithmException Si el elemento insertado no forma parte del
     * conjunto de elementos no terminales.
     */
    public void setStartSymbol(char nonterminal) throws CYKAlgorithmException {
        if(!nonTerminals.contains(nonterminal)){
            throw new CYKAlgorithmException();
        }
        axioma=nonterminal;

    }

    @Override
    /**
     * Método utilizado para construir la gramática. Admite producciones en FNC,
     * es decir de tipo A::=BC o A::=a
     *
     * @param nonterminal A
     * @param production "BC" o "a"
     * @throws CYKAlgorithmException Si la producción no se ajusta a FNC o está
     * compuesta por elementos (terminales o no terminales) no definidos
     * previamente.
     */
    public void addProduction(char nonterminal, String production) throws CYKAlgorithmException {
        //comprobamos que el no terminal esta en el arraylist de los no terminales
        if(!nonTerminals.contains(nonterminal)){
            throw new CYKAlgorithmException();
        }
        //comprobamos que la produccion es de tamaño 1 o 2
        if(production.length()>2||production.length()<1){
            throw new CYKAlgorithmException();
        }
        //comprobamos que la produccion se ajusta a FNC
        if(production.length()==1){
            if(!Terminals.contains(production.charAt(0))){
                throw new CYKAlgorithmException();
            }
        }
        if(production.length()==2){
            if(!nonTerminals.contains(production.charAt(0))||!nonTerminals.contains(production.charAt(1))){
                throw new CYKAlgorithmException();
            }
        }
        //si pasa todas las condiciones de este metodo se añade la produccion
        producciones.put(nonterminal,production);



    }
    //creamos un metodo para que nos cree la matriz
    public String[][] crearMatriz(String word){
        //creamos la matriz
        int n=word.length();
        String[][] matriz= new String[n][n];
        //rellenamos la matriz
        for(int i=0;i<n;i++){
            for(int j=0;j<n;j++){
                matriz[i][j]="";
            }
        }
        //rellenamos la diagonal
        for(int i=0;i<n;i++){
            matriz[i][i]=Character.toString(word.charAt(i));
        }
        //rellenamos la matriz
        for(int i=1;i<n;i++){
            for(int j=0;j<n-i;j++){
                for(int k=0;k<i;k++){
                    for(int l=0;l<producciones.get(axioma).length();l++){
                        if(matriz[j][j+k].contains(Character.toString(producciones.get(axioma).charAt(l)))){
                            matriz[j][j+i]+=producciones.get(axioma).charAt(0);
                        }
                    }
                }
            }
        }
        return matriz;
    }

    @Override
    /**
     * Método que indica si una palabra pertenece al lenguaje generado por la
     * gramática que se ha introducido.
     *
     * @param word La palabra a verificar, tiene que estar formada sólo por
     * elementos no terminales.
     * @return TRUE si la palabra pertenece, FALSE en caso contrario
     * @throws CYKAlgorithmException Si la palabra proporcionada no está formada
     * sólo por terminales, si está formada por terminales que no pertenecen al
     * conjunto de terminales definido para la gramática introducida, si la
     * gramática es vacía o si el autómata carece de axioma.
     */
    public boolean isDerived(String word) throws CYKAlgorithmException {
        //comprobamos que todos los elementos de la palabra word pertenecen al conjunto de los terminales
        for(int i=0;i<word.length();i++){
            if(!Terminals.contains(word.charAt(i))){
                throw new CYKAlgorithmException();
            }
        }
        //comprobamos que la gramatica no es vacia
        if(nonTerminals.isEmpty()){
            throw new CYKAlgorithmException();
        }
        //comprobamos que el automata tiene axioma
        if(axioma=='\u0000'){
            throw new CYKAlgorithmException();
        }

        //comprobamos si la palabra pertenece al lenguaje generado por la gramatica llamando al metodo crearMatriz
        crearMatriz(word);
        if(crearMatriz(word)[0][word.length()-1].contains(Character.toString(axioma))){
            return true;
        }

        return false;



    }

    @Override
    /**
     * Método que, para una palabra, devuelve un String que contiene todas las
     * celdas calculadas por el algoritmo (la visualización debe ser similar al
     * ejemplo proporcionado en el PDF que contiene el paso a paso del
     * algoritmo).
     *
     * @param word La palabra a verificar, tiene que estar formada sólo por
     * elementos no terminales.
     * @return Un String donde se vea la tabla calculada de manera completa,
     * todas las celdas que ha calculado el algoritmo.
     * @throws CYKAlgorithmException Si la palabra proporcionada no está formada
     * sólo por terminales, si está formada por terminales que no pertenecen al
     * conjunto de terminales definido para la gramática introducida, si la
     * gramática es vacía o si el autómata carece de axioma.
     */
    public String algorithmStateToString(String word) throws CYKAlgorithmException {
        //comprbamos que word esta formada solo por terminales que pertenecen al conjunto de terminales
        for(int i=0;i<word.length();i++){
            if(!Terminals.contains(word.charAt(i))){
                throw new CYKAlgorithmException();
            }
        }
        //comprobamos que la gramatica no es vacia
        if(nonTerminals.isEmpty()){
            throw new CYKAlgorithmException();
        }
        //comprobamos que el automata tiene axioma
        if(axioma=='\u0000'){
            throw new CYKAlgorithmException();
        }
        //llamamos al metodo crearMatriz para que nos cree la matriz de la palabra word
        crearMatriz(word);
        String matriz="";
        //rellenamos el string con la matriz
        for(int i=0;i<word.length();i++){
            for(int j=0;j<word.length();j++){
                matriz+=crearMatriz(word)[i][j];
            }
            matriz+="\n";
        }
        return matriz;

    }

    @Override
    /**
     * Elimina todos los elementos que se han introducido hasta el momento en la
     * gramática (elementos terminales, no terminales, axioma y producciones),
     * dejando el algoritmo listo para volver a insertar una gramática nueva.
     */
    public void removeGrammar() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    /**
     * Devuelve un String que representa todas las producciones que han sido
     * agregadas a un elemento no terminal.
     *
     * @param nonterminal
     * @return Devuelve un String donde se indica, el elemento no terminal, el
     * símbolo de producción "::=" y las producciones agregadas separadas, única
     * y exclusivamente por una barra '|' (no incluya ningún espacio). Por
     * ejemplo, si se piden las producciones del elemento 'S', el String de
     * salida podría ser: "S::=AB|BC".
     */
    public String getProductions(char nonterminal) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    /**
     * Devuelve un String con la gramática completa.
     *
     * @return Devuelve el agregado de hacer getProductions sobre todos los
     * elementos no terminales.
     */
    public String getGrammar() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
