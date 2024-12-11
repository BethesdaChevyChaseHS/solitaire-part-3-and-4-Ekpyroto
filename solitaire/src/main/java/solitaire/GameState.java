package solitaire;

import java.util.ArrayList;
import java.util.Stack;

public class GameState {
    private Stack<Card> deck; // Full deck of cards
    private Stack<Card>[] gamePiles; // Seven piles on the tableau
    private Stack<Card> visibleCards; // Stack for visible cards
    private Stack<Card> discardedCards; // Discard pile
    private Stack<Card>[] foundationPiles; // Four foundation piles

    @SuppressWarnings("unchecked")
    public GameState() {
        // Initialize the game state
        deck = new Stack<>();
        gamePiles = new Stack[7]; // Array of 7 stacks
        visibleCards = new Stack<>();
        discardedCards = new Stack<>();

        // Initialize each game pile
        for (int i = 0; i < gamePiles.length; i++) {
            gamePiles[i] = new Stack<>();
        }
        foundationPiles = new Stack[4];
        for (int i = 0; i < foundationPiles.length; i++) {
            foundationPiles[i] = new Stack<>();
        }

        initializeDeck();
        shuffleDeck();
        dealInitialCards();
    }

    //REPLACE THE FOLLOWING 4 functions with your code from part 2

    // Creates a full deck of cards with all combinations of suits and ranks
    private void initializeDeck() {
      //USE IMPLEMENTATION FROM PART 2
      for(Rank r : Rank.values()){
        for(Suit s : Suit.values()){
            deck.push(new Card(s,r));
        }
    }

    }

    // Shuffles the deck
    private void shuffleDeck() {
        java.util.Collections.shuffle(deck);
    }

    // Deals cards to the 7 game piles
    private void dealInitialCards() {
        //USE IMPLEMENTATION FROM PART 2
        for(int i =0; i<gamePiles.length; i++){
            for(int j =0; j<=i; j++){
                gamePiles[i].push(deck.pop());
            }
            gamePiles[i].peek().flip();
        }

    }

    // Draws up to three cards from the deck into visibleCards
    public void drawFromDeck() {
        //USE IMPLEMENTATION FROM PART 2
        discardCards();
        for(int i = 0; i<3; i++){
            visibleCards.push(deck.pop());
            visibleCards.peek().flip();
        }
        if(deck.size()==0){
            for(int i = discardedCards.size(); i>0;i--){
                deck.push(discardedCards.pop());
            }
        }
    }

    public void discardCards() {
        //takes whatever cards are remaining in the visibleCards pile and moves them to the discardPiles
        for(int i = 0; i<visibleCards.size(); i++){
            discardedCards.push(visibleCards.pop());
        }
    }

    // new methods from part 3

    public boolean canCardMove(Card card, int toPile){
        /*a card can be moved from the visible cards to a pile if 
            A) The card is the opposite color and its rank is ONE smaller than the card it will be placed on
            B) The pile is empty and the card is a King
        */
        if(gamePiles[toPile].size()==0&&card.getRank()==Rank.KING){
            return true;
        }
        if(card.getColor()!=gamePiles[toPile].peek().getColor()&&card.getRank().ordinal()+1==gamePiles[toPile].peek().getRank().ordinal()){
            return true;
        }
        return false;
    }
    // attempts to move top card from visible card stack to the toPileIndex
    // returns true if successful and false if unsuccessful
    public boolean moveCardFromVisibleCardsToPile(int toPileIndex) {
        /* 
            If a card can be moved, it should be popped from the visible cards pile and pushed to the pile it is added to
            hints: use peek() and ordinal() to determine whether or not a card can be moved. 
            USE the method you just made, canCardMove
        */
        if(canCardMove(visibleCards.peek(), toPileIndex)){
            gamePiles[toPileIndex].push(visibleCards.pop());
            return true;
        }
        return false;
    }

    // Move a card from one pile to another
    public boolean moveCards(int fromPileIndex, int cardIndex, int toPileIndex) {
        Stack<Card> fromPile = gamePiles[fromPileIndex];

        // Create a sub-stack of cards to move
        ArrayList<Card> cardsToMove = new ArrayList<>(fromPile.subList(cardIndex, fromPile.size()));

        Card bottomCard = cardsToMove.get(0); // the bottom card to be moved

        // Check if bottomCard can be moved to the toPile
        // if we can move the cards, add cardsToMove to the toPile and remove them from the fromPile
        // Then, flip the next card in the fromPile stack

        //return true if successful, false if unsuccessful

        if(canCardMove(bottomCard,toPileIndex)){
            for(int i = cardsToMove.size();i>0;i++){
                gamePiles[toPileIndex].push(gamePiles[fromPileIndex].pop());
            }
        }
        else{
            return false;
        }
        gamePiles[fromPileIndex].peek().flip();
        return true;
    }
    public boolean canMoveToFoundation(Card card, int foundationIndex){
        //The foundation piles are the 4 piles that you have to build to win the game. 
        //In order for a card to be added to the pile, it needs to be one larger than the 
        //current top card of the foundation pile. It needs to be the same suit. 
        //If the foundation pile is empty, the new card must be an ace

        //This method should return true if a card can be moved to the foundation, and false otherwise. 
        
        //hint: another good time to use peek() and ordinal()
        if(foundationPiles[foundationIndex].size()==0&&card.getRank()==Rank.ACE){
            return true;
        }
        if(card.getSuit()==foundationPiles[foundationIndex].peek().getSuit()&&card.getRank().ordinal()-1==foundationPiles[foundationIndex].peek().getRank().ordinal()){
            return true;
        }
        return false;
    }
    public boolean moveToFoundation(int fromPileIndex, int foundationIndex) {
        //check if we can move the top card of the fromPile to the foundation at foundationIndex
        if(!(canMoveToFoundation(gamePiles[fromPileIndex].peek(),foundationIndex))){return false;}
        //remember to flip the new top card if it is face down
        foundationPiles[foundationIndex].push(gamePiles[fromPileIndex].pop());
        if(!gamePiles[fromPileIndex].peek().isFaceUp()){
            gamePiles[fromPileIndex].peek().flip();
            
        }
        //return true if successful, false otherwise
        return true;
    }

    public boolean moveToFoundationFromVisibleCards(int foundationIndex) {
        //similar to the above method, 
        //move the top card from the visible cards to the foundation pile with index foundationIndex if possible
    
        //return true if successful, false otherwise. 
        if(!(canMoveToFoundation(visibleCards.peek(),foundationIndex))){return false;}

        foundationPiles[foundationIndex].push(visibleCards.pop());
        if(!visibleCards.peek().isFaceUp()){
            visibleCards.peek().flip();
            
        }
        //return true if successful, false otherwise
        return true;
    }

    

    // Don't change this, used for testing
    public void printState() {
        System.out.println("Deck size: " + deck.size());

        System.out.print("Visible cards: ");
        if (visibleCards.isEmpty()) {
            System.out.println("None");
        } else {
            for (Card card : visibleCards) {
                System.out.print(card + " ");
            }
            System.out.println();
        }

        System.out.println("Discarded cards: " + discardedCards.size());

        System.out.println("Game piles:");
        for (int i = 0; i < gamePiles.length; i++) {
            System.out.print("Pile " + (i + 1) + ": ");
            if (gamePiles[i].isEmpty()) {
                System.out.println("Empty");
            } else {
                for (Card card : gamePiles[i]) {
                    System.out.print(card + " ");
                }
                System.out.println();
            }
        }
    }

    // getters
    public Stack<Card> getGamePile(int index) {
        return gamePiles[index];
    }

    public Stack<Card> getFoundationPile(int index) {
        return foundationPiles[index];
    }

    public Stack<Card> getDeck() {
        return deck;
    }

    public Stack<Card> getVisibleCards() {
        return visibleCards;
    }
}
