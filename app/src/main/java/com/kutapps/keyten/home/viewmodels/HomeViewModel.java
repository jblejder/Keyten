package com.kutapps.keyten.home.viewmodels;

import android.databinding.ObservableField;

import com.google.firebase.database.DatabaseError;
import com.kutapps.keyten.KeytenApp;
import com.kutapps.keyten.home.models.LoggedUserModel;
import com.kutapps.keyten.main.viewmodels.MainViewModel;
import com.kutapps.keyten.shared.MessagesHelper;
import com.kutapps.keyten.shared.constants.State;
import com.kutapps.keyten.shared.database.DatabaseHelper;
import com.kutapps.keyten.shared.database.models.KeytenModel;

import org.joda.time.DateTime;

public class HomeViewModel
{
    public final ObservableField<LoggedUserModel> user;
    public final ObservableField<State>           state;

    private final DatabaseHelper databaseHelper;
    private final MessagesHelper messagesHelper;


    {
        state = new ObservableField<>(State.Init);
    }

    public HomeViewModel(MainViewModel rootModel)
    {
        user = rootModel.user;
        databaseHelper = KeytenApp.getDatabaseHelper();
        messagesHelper = KeytenApp.getMessagesHelper();

        messagesHelper.registerForKeytens();
        initDatabase();
    }

    private void initDatabase()
    {
        databaseHelper.listenKeytenChange(new DatabaseHelper.DbListener<KeytenModel>()
        {
            @Override
            public void newValue(KeytenModel model)
            {
                if (model != null)
                {
                    state.set(model.value ? State.Keyten : State.Noten);
                }
                else
                {
                    state.set(State.Noten);
                }
            }

            @Override
            public void error(DatabaseError error)
            {

            }
        });
    }

    public void setKeyten()
    {
        State state = this.state.get();
        KeytenModel model = new KeytenModel(state != State.Keyten, DateTime.now(), "JA");

        if (state.equals(State.Noten))
        {
            messagesHelper.sendKeytenMessage(model);
        }
        databaseHelper.setKeyten(model);
    }

}
