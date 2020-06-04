package com.example.pokeroddscalculator;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.channels.SelectableChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HandCalculatorActivity extends AppCompatActivity {

    private Spinner handSpinner;
    private ImageView heroCard1,heroCard2,villainCard1,villainCard2,comCard1,comCard2,comCard3,comCard4,comCard5;
    private Button btnBack,btnClear;
    private String selectedCard, sRank, sSuit;
    private Byte rank,suit;
    private TextView handOutput;
    private List<String> fullDeck,deckwithmissingCards;
    private List<Card> heroCards, villainCards;
    private Card currentCard;
    private int currentcardIndex;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateUI();
        setContentView(R.layout.activity_hand_calculator);

        Toolbar toolbar = findViewById(R.id.handcalculatortoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Hand Analyser");
        toolbar.setTitleTextColor(Color.WHITE);

        //initialising fields
        heroCard1 = (ImageView) findViewById(R.id.herocard1);
        heroCard2 = (ImageView) findViewById(R.id.herocard2);
        villainCard1 = (ImageView) findViewById(R.id.villaincard1);
        villainCard2 = (ImageView) findViewById(R.id.villaincard2);
        comCard1 = (ImageView) findViewById(R.id.communitycard1);
        comCard2 = (ImageView) findViewById(R.id.communitycard2);
        comCard3 = (ImageView) findViewById(R.id.communitycard3);
        comCard4 = (ImageView) findViewById(R.id.communitycard4);
        comCard5 = (ImageView) findViewById(R.id.communitycard5);

        initialiseSpinner();
        fullDeck = new ArrayList<>();
        deckwithmissingCards = new ArrayList<>();
        heroCards = new ArrayList<>();
        villainCards = new ArrayList<>();

        String[] stringArray = getResources().getStringArray(R.array.cards);
        Collections.addAll(fullDeck,stringArray);

        btnBack = findViewById(R.id.backtohome5);
        btnClear = findViewById(R.id.clearvalues);
        handOutput = findViewById(R.id.handoutput);

        //setting variable whenever a card is clicked
        handSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selectedCard = parent.getItemAtPosition(pos).toString();
                getcardValue(selectedCard);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                selectedCard = "";
            }

        });

        heroCard1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(selectedCard.isEmpty())){ //if a hand is selected
                    placeCard(currentCard,selectedCard,heroCard1);
                    checkhandRanking();
                }
            }
        });

        heroCard2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(selectedCard.isEmpty())){ //if a hand is selected
                    placeCard(currentCard,selectedCard,heroCard2);
                    checkhandRanking();
                }
            }
        });

        villainCard1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(selectedCard.isEmpty())){ //if a hand is selected
                    placeCard(currentCard,selectedCard,villainCard1);
                    checkhandRanking();
                }
            }
        });

        villainCard2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(selectedCard.isEmpty())){ //if a hand is selected
                    placeCard(currentCard,selectedCard,villainCard2);
                    checkhandRanking();
                }
            }
        });

        comCard1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(selectedCard.isEmpty())){ //if a hand is selected
                    placeCard(currentCard,selectedCard,comCard1);
                    checkhandRanking();
                }
            }
        });

        comCard2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(selectedCard.isEmpty())){ //if a hand is selected
                    placeCard(currentCard,selectedCard,comCard2);
                    checkhandRanking();
                }
            }
        });

        comCard3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(selectedCard.isEmpty())){ //if a hand is selected
                    placeCard(currentCard,selectedCard,comCard3);
                    checkhandRanking();
                }
            }
        });

        comCard4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(selectedCard.isEmpty())){ //if a hand is selected
                    placeCard(currentCard,selectedCard,comCard4);
                    checkhandRanking();
                }
            }
        });

        comCard5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(selectedCard.isEmpty())){ //if a hand is selected
                    placeCard(currentCard,selectedCard,comCard5);
                    checkhandRanking();
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  //back to home
                Intent intent = new Intent(HandCalculatorActivity.this,HomePageActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  //back to home
                initialiseSpinner();

                fullDeck.clear();
                deckwithmissingCards.clear();

                String[] stringArray = getResources().getStringArray(R.array.cards);
                Collections.addAll(fullDeck,stringArray);

                deckwithmissingCards = fullDeck;

                heroCards.clear();
                villainCards.clear();

                heroCard1.setImageResource(R.drawable.backofcard);
                heroCard1.setTag(null);
                heroCard2.setImageResource(R.drawable.backofcard);
                heroCard2.setTag(null);
                villainCard1.setImageResource(R.drawable.backofcard);
                villainCard1.setTag(null);
                villainCard2.setImageResource(R.drawable.backofcard);
                villainCard2.setTag(null);
                comCard1.setImageResource(R.drawable.backofcard);
                comCard1.setTag(null);
                comCard2.setImageResource(R.drawable.backofcard);
                comCard2.setTag(null);
                comCard3.setImageResource(R.drawable.backofcard);
                comCard3.setTag(null);
                comCard4.setImageResource(R.drawable.backofcard);
                comCard4.setTag(null);
                comCard5.setImageResource(R.drawable.backofcard);
                comCard5.setTag(null);

            }
        });

    }

    public void getcardValue(String selectedCard){

        if (selectedCard.length() == 2){
            sRank = selectedCard.substring(0,1);
            sSuit = selectedCard.substring(1,2);
        } else if (selectedCard.length() == 3){
            sRank = selectedCard.substring(0,2);
            sSuit = selectedCard.substring(2,3);
        }

        switch (sRank) {
            case "A":
                rank = 0;
                break;
            case "2":
                rank = 1;
                break;
            case "3":
                rank = 2;
                break;
            case "4":
                rank = 3;
                break;
            case "5":
                rank = 4;
                break;
            case "6":
                rank = 5;
                break;
            case "7":
                rank = 6;
                break;
            case "8":
                rank = 7;
                break;
            case "9":
                rank = 8;
                break;
            case "10":
                rank = 9;
                break;
            case "J":
                rank = 10;
                break;
            case "Q":
                rank = 11;
                break;
            case "K":
                rank = 12;
                break;
        }

        switch (sSuit) {
            case "s":
                suit = 0;
                break;
            case "h":
                suit = 1;
                break;
            case "c":
                suit = 2;
                break;
            case "d":
                suit = 3;
                break;
        }
        currentCard = new Card(suit,rank);
    }

    public void placeCard(Card currentCard, String selectedCard, ImageView cardImage){
        currentcardIndex = 99999;
        if (currentCard.getSuit() == 0){ //will be from 0th element to 12th element
            currentcardIndex = currentCard.getRank();
        } else if (currentCard.getSuit() == 1){
            currentcardIndex = 13+currentCard.getRank();
        } else if (currentCard.getSuit() == 2){
            currentcardIndex = 26+ currentCard.getRank();
        } else if (currentCard.getSuit() == 3){
            currentcardIndex = 39+ currentCard.getRank();
        } else {
            currentcardIndex=9999;
        }

        if (cardImage.getTag() == null){
            String lowercaseCard = selectedCard.toLowerCase(); //turn lowercase to get sprites for cards
            lowercaseCard = "_" + lowercaseCard;

            int resImage = getResources().getIdentifier(lowercaseCard , "drawable", getPackageName());
            cardImage.setImageResource(resImage); //change image
            cardImage.setTag(R.drawable._qs);

            deckwithmissingCards = fullDeck;
            deckwithmissingCards.set(currentcardIndex,""); //set removed cards to be null

            ArrayAdapter<String> new_adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, deckwithmissingCards);
            new_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            handSpinner.setAdapter(new_adapter);

            if (cardImage == heroCard1 || cardImage == heroCard2){ //if adding to hero card
                heroCards.add(currentCard);
            } else if (cardImage == villainCard1 || cardImage == villainCard2){ //if adding to villain cards
                villainCards.add(currentCard);
            } else {
                heroCards.add(currentCard);
                villainCards.add(currentCard);
            }


        }
    }

    public void checkhandRanking(){
        if (heroCards.size()>=5||villainCards.size()>=5){ //if theres enough cards to see hand ranking
            //DO THE LOGIC
        }
    }

    public void initialiseSpinner(){
        handSpinner = (Spinner) findViewById(R.id.selectcard);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.cards, android.R.layout.simple_spinner_item); //list of positions is stored in table_positions in res/values folder
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        handSpinner.setAdapter(adapter);
    }


    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    //code taken from stackexchange to hide nav bar after using keyboard
    public void updateUI() {
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
