package server;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * server.Challenge
 */
public class Challenge {
    private User friend;
    private final LinkedList<String> wordsToTranslate;
    private final LinkedList<ArrayList<String>> wordsTranslated;
    private int position;
    private final TranslateService ts;
    private Date start = null;
    private int guessed=0;
    private final AtomicBoolean started;

    public Challenge() {
        started=new AtomicBoolean(false);
        this.ts = new TranslateService();
        position = 0;
        wordsToTranslate=new LinkedList<>();
        wordsTranslated=new LinkedList<>();
    }

    public String nextWordToTranslate() {
        if (start == null)
            start = new Date();

        int millisecondsToComplete = 60000;
        boolean isFinished = new Date().getTime() - start.getTime() > millisecondsToComplete;
        if (!isFinished) {
            if (position < wordsToTranslate.size()) {
                position++;
                return wordsToTranslate.get(position - 1);
            } else {
                return "Sfida terminata";
            }
        }
        return "Tempo scaduto";
    }

    public int addWord(String word) {
        if (wordsTranslated.get(position -1).contains(word.trim().toLowerCase())) {
            guessed++;
            return 0;
        }
        return 1;
    }

    public int getGuessed() {
        return guessed;
    }

    public User getFriend() {
        return friend;
    }

    private void setStarted(boolean started) {
        this.started.set(started);
    }

    public boolean isStarted(){
        return started.get();
    }

    private void setFriend(User friend) {
        this.friend = friend;
    }

    private void translateWords(){
        for (String s : wordsToTranslate) {
            ArrayList<String> res = ts.translate(s);
            wordsTranslated.add(res);
        }
    }

    public void start(User friend, List<String> words) {
        setStarted(true);
        start = null;
        position=0;
        guessed=0;
        wordsTranslated.clear();
        wordsToTranslate.clear();
        wordsToTranslate.addAll(words);
        translateWords();
        setFriend(friend);
    }

    public void stop() {
        setStarted(false);
    }


}