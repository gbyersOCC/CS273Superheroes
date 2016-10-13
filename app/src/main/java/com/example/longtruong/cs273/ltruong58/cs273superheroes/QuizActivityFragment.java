package com.example.longtruong.cs273.ltruong58.cs273superheroes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.content.Context;

/**
 * A placeholder fragment containing a simple view.
 */
public class QuizActivityFragment extends Fragment {

    private static final String TAG = "FlagQuiz Activity";
    private static final int SUPERHEROS_IN_QUIZ = 10;
    private static final int GUESS_ROWS = 2;
    private Context context; // for json data loading
    private ArrayList<Superhero> allSuperheroes; // store data loading from json file
    private ArrayMap<String, String> superheroList; // store  images of superheroes(K) and quizAnswer (V), only the superhero in quiz are stored here
    private List<String> quizSuperheroList; // store all the quiz answers from all superhero in json file
    private String correctAnswer; // quizAnswer
    private int totalGuesses; // incorrect + correct guesses
    private int correctAnswers; // number of correct answers
    String quizType = null; // (superhero name / superpower / onething) to get quizAnswer
    private SecureRandom random;
    private Handler handler;

    private TextView questionNumberTextView;
    private ImageView superHeroImageView;
    private LinearLayout[] guessLinearLayouts;
    private TextView answerTextView;
    private TextView guessHeroTextView;

    public QuizActivityFragment() {
    }

    /** Configure views when the fragment create
     * @param inflater layout inflater
     * @param container view
     * @param savedInstanceState bundle
     * @return view that run this fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);
        quizSuperheroList = new ArrayList<>();
        superheroList = new ArrayMap<>();
        allSuperheroes = new ArrayList<>();

        random = new SecureRandom();
        handler = new Handler();
        context = this.getContext();

        //get data directly from Json
        try {
            allSuperheroes = JSONLoader.loadJSONFromAsset(context);
        } catch (IOException e) {
            Log.e("Superheros", "Error loading Json data." + e.getMessage());
        }

        // Configure views
        questionNumberTextView = (TextView) view.findViewById(R.id.questionNumberTextView);
        superHeroImageView = (ImageView) view.findViewById(R.id.superHeroImageView);
        guessLinearLayouts = new LinearLayout[2];
        guessLinearLayouts[0] = (LinearLayout) view.findViewById(R.id.row1LinearLayout);
        guessLinearLayouts[1] = (LinearLayout) view.findViewById(R.id.row2LinearLayout);
        guessHeroTextView = (TextView) view.findViewById(R.id.guessHeroTextView);
        answerTextView = (TextView) view.findViewById(R.id.answerTextView);

        // Configure listeners for the guess Buttons
        for(LinearLayout row: guessLinearLayouts) {
            for(int column = 0; column < row.getChildCount(); column++) {
                Button button = (Button) row.getChildAt(column);
                button.setOnClickListener(guessButtonListener);
            }
        }

        // set questionNumberTextView's first text
        questionNumberTextView.setText(getString(R.string.question, 1, SUPERHEROS_IN_QUIZ));

        return view;
    }

    /**
     * Reset all the settings when the preference changed or loading new quiz
     */
    public void resetQuiz() {
        // Reset and update
        updateQuizSuperheroList();
        superheroList.clear();
        correctAnswers = 0;
        totalGuesses = 0;

        int superheroCounter = 1;
        int numberOfSuperheroes = allSuperheroes.size();

        // Load new data for superheroList
        while (superheroCounter <= SUPERHEROS_IN_QUIZ) {
            int randomIndex = random.nextInt(numberOfSuperheroes);
            String filename = allSuperheroes.get(randomIndex).getImageName();
            String quizText = allSuperheroes.get(randomIndex).getButtonText(quizType);
            if(!superheroList.containsKey(filename)) {
                superheroList.put(filename, quizText);
                ++superheroCounter;
            }
        }
        loadNextSuperhero();
    }

    /**
     * Load new data for views after resetQuiz called or user clicks on the correct answer button.
     */
    private  void loadNextSuperhero() {
        String nextImage = superheroList.keyAt(0);
        correctAnswer = superheroList.get(nextImage);
        superheroList.remove(nextImage);
        answerTextView.setText("");

        switch (quizType)
        {
            case Superhero.SUPERHERO_NAME:
                guessHeroTextView.setText(R.string.guess_superhero);
                break;
            case Superhero.SUPERPOWER:
                guessHeroTextView.setText(R.string.guess_superpower);
                break;
            case Superhero.ONE_THING:
                guessHeroTextView.setText(R.string.guess_one_thing);
                break;
        }

        questionNumberTextView.setText(getString(R.string.question, (correctAnswers + 1), SUPERHEROS_IN_QUIZ));

        AssetManager assets = getActivity().getAssets();

        try (InputStream stream = assets.open( nextImage)) {
            Drawable superhero = Drawable.createFromStream(stream, nextImage);
            superHeroImageView.setImageDrawable(superhero);
        } catch (IOException e) {
            Log.e(TAG, "Error loading " + nextImage, e);
        }

        Collections.shuffle(quizSuperheroList);

        // Bring correct answer  to the end of the array
        int correct = quizSuperheroList.indexOf(correctAnswer);
        quizSuperheroList.add(quizSuperheroList.remove(correct));

        // Set incorrect answer for buttons to display
        for (int row =0; row < GUESS_ROWS; row++) {
            for ( int column = 0; column < guessLinearLayouts[row].getChildCount(); column++) {
                Button newGuessButton = (Button) guessLinearLayouts[row].getChildAt(column);
                newGuessButton.setEnabled(true);
                String buttonText = "";
                buttonText= quizSuperheroList.get((row * 2) + column);
                newGuessButton.setText(buttonText);
            }
        }

        // Set the correct answer
        int row = random.nextInt(GUESS_ROWS);
        int column = random.nextInt(2);
        LinearLayout randomRow = guessLinearLayouts[row];
        String buttonText = correctAnswer;
        ((Button) randomRow.getChildAt(column)).setText(buttonText);
    }

    private View.OnClickListener guessButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button guessButton = ((Button) v);
            String guess = guessButton.getText().toString();
            String answer = correctAnswer;
            ++totalGuesses;

            if(guess.equals(answer)) {
                ++correctAnswers;

                answerTextView.setText(answer + "!");
                answerTextView.setTextColor(getResources().getColor(R.color.correct_answer, getContext().getTheme()));

                disableButtons();

                if(correctAnswers == SUPERHEROS_IN_QUIZ) {
                    DialogFragment quizResults = new DialogFragment() {
                        @Override
                        public Dialog onCreateDialog(Bundle bundle) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage(getString(R.string.results, totalGuesses, (100 * SUPERHEROS_IN_QUIZ/ (double) totalGuesses)));

                            builder.setPositiveButton(R.string.reset_quiz, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            resetQuiz();
                                        }
                                    }
                            );
                            return builder.create();
                        }
                    };
                    quizResults.setCancelable(false);
                    quizResults.show(getFragmentManager(), "quiz results");
                }
                else {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadNextSuperhero();
                        }
                    }, 2000);
                }
            }
            else {
                answerTextView.setText(R.string.incorrect_answer);
                answerTextView.setTextColor(getResources().getColor(R.color.incorrect_answer,getContext().getTheme()));
                guessButton.setEnabled(false);
            }
        }
    };

    /**
     * Called when user clicks on the correct button
     */
    private void disableButtons() {
        for(int row = 0; row < GUESS_ROWS; row++) {
            LinearLayout guessRow = guessLinearLayouts[row];
            for(int column = 0; column < guessRow.getChildCount(); column++) {
                guessRow.getChildAt(column).setEnabled(false);
            }
        }
    }

    /**
     * Update quizType when preference changed
     * @param sharedPreferences preference from Quiz Activity
     */
    public void updateQuizType(SharedPreferences sharedPreferences)
    {
        quizType = sharedPreferences.getString(QuizActivity.TYPES, null);
    }

    /**
     * Update quizSuperheroList when activity created or quizType changed
     */
    public  void updateQuizSuperheroList() {
        quizSuperheroList.clear();
        for(int i = 0; i < allSuperheroes.size(); i++)
        {
            quizSuperheroList.add(allSuperheroes.get(i).getButtonText(quizType));
        }
    }
}

