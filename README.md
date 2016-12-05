###Министерство образования российской федерации
####Факультет: Информатики и системы управления
####Кафедра: Информационная безопасность
###Алгоритмические языки программирования
####Домашнее задание No1
####«Курьерская доставка.»

Руководитель дисциплины: Бородин А. А.

Студент: Саркисян А. О.

###Цель работы
Создать приложение, на платформе android, которое позволяет сделать заказ на курьерскую доставку чего-либо наиблоее простым для пользователя способом. 
###Особенности прогарммы
Особенностью прогарммы является простота действия, которая позволяет пользователю за минимум действий сделать заказ и получить ответ в кратчайшие сроки. Так же особенностью ялвяется живое общение, которое может происходить, как в удобном чате, так и по телефну, который опретор узнает из заполненной анкеты. 

###Работа приложения
Приложение "ArcBox" целиком работает на облачном хранилище Google - [firebase](https://firebase.google.com). Это удобное и прсотое в использвоании облачное, NoSQLite хранилище. При любом действии в программе приложение обращается к firebase и только после получения ответа выводит данные пользователю. После оформления заказа во Вкладке "Form" пользователю сообщается, что с ним, в скором времни, свяжется оператор. После подтвержения заказа оператором, пользвоатель поулчает уведомление о готовности курьера к выезду. В чате он может указать подробности по заказу или изменить существующие опции, там же ему будет объявлена стоимость данной доставки. Если все согласовано, то, в скором времени, курьер будет у пользователя. Со стороны приложения - идет полная гарантия на успешную доставку товара или возмещение ущерба в размере ее стоимости, плюс компенсация. После доставки товара пользователь поулчает уведомление о достаке товара, где он может сообщить о возникших проблемах. Ему будут предложены пути решения. 

###Разбор кода приложения
Приложение состоит из трех вкладок: "How to use?", "FORM" и "CHAT". Функциональность описана выше. 

**AuthorizationActivity.java - класс отвечающий за авторизацию в приложении**

    public class AuthorizationActivity extends AppCompatActivity implements
            GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

        Animation anim = null;
        private static final int RC_SIGN_IN = 9001;
        private SignInButton mAuthButton;
        private FirebaseAuth mFirebaseAuth;
        private GoogleApiClient mGoogleApiClient;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_authorization);

            anim = AnimationUtils.loadAnimation(this, R.anim.myalpha);
            TextView tvHello, tvAuth, tvProjBy;
            tvHello = (TextView) findViewById(R.id.tvHello);
            tvAuth = (TextView) findViewById(R.id.tvAuth);
            tvProjBy = (TextView) findViewById(R.id.tvProjBy);
            tvHello.startAnimation(anim);
            tvAuth.startAnimation(anim);
            tvProjBy.startAnimation(anim);

            mAuthButton = (SignInButton) findViewById(R.id.auth_button);
            mAuthButton.setOnClickListener(this);

            mFirebaseAuth = FirebaseAuth.getInstance();

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.auth_button:
                    Authorize();
                    break;
            }
        }

        private void Authorize() {
            Intent authorizeIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(authorizeIntent, RC_SIGN_IN);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == RC_SIGN_IN) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {
                    GoogleSignInAccount account = result.getSignInAccount();
                    firebaseAuthWithGoogle(account);
                } else {
                    // Google Sign In failed
                }
            }
        }

        private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            mFirebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Toast.makeText(AuthorizationActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                startActivity(new Intent(AuthorizationActivity.this, MainActivity.class));
                                finish();
                            }
                        }
                    });
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
        }
    }
    
**ChatActivity.java - класс отвечающий за окно чата**

public class ChatActivity extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

        private DatabaseReference mSimpleFirechatDatabaseReference;
        private FirebaseRecyclerAdapter<ChatMessage, FirechatMsgViewHolder>
                mFirebaseAdapter;
        private RecyclerView mMessageRecyclerView;
        private LinearLayoutManager mLinearLayoutManager;
        private ProgressBar mProgressBar;

        private Button mSendButton;
        private String mUsername;
        private String mPhotoUrl;
        private EditText mMsgEditText;

        private GoogleApiClient mGoogleApiClient;
        private FirebaseAuth mFirebaseAuth;
        private FirebaseUser mFirechatUser;

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Toast.makeText(getActivity(), "Google Play Services error.", Toast.LENGTH_SHORT).show();
        }

        public static class FirechatMsgViewHolder extends RecyclerView.ViewHolder {
            public TextView msgTextView;
            public TextView userTextView;
            public CircleImageView userImageView;

            public FirechatMsgViewHolder(View v) {
                super(v);
                msgTextView = (TextView) itemView.findViewById(R.id.msgTextView);
                userTextView = (TextView) itemView.findViewById(R.id.userTextView);
                userImageView = (CircleImageView) itemView.findViewById(R.id.userImageView);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.activity_chat, container, false);


            mFirebaseAuth = FirebaseAuth.getInstance();
            mFirechatUser = mFirebaseAuth.getCurrentUser();
            if (mFirechatUser == null) {
                startActivity(new Intent(getActivity(), AuthorizationActivity.class));
                //finish();
                //return;
            } else {
                mUsername = mFirechatUser.getDisplayName();
                if (mFirechatUser.getPhotoUrl() != null) {
                    mPhotoUrl = mFirechatUser.getPhotoUrl().toString();
                }
            }

            mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
            mMessageRecyclerView = (RecyclerView) rootView.findViewById(R.id.messageRecyclerView);
            mLinearLayoutManager = new LinearLayoutManager(getActivity());
            mLinearLayoutManager.setStackFromEnd(true);
            mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

            mSimpleFirechatDatabaseReference = FirebaseDatabase.getInstance().getReference();
            mFirebaseAdapter = new FirebaseRecyclerAdapter<ChatMessage,
                    FirechatMsgViewHolder>(
                    ChatMessage.class,
                    R.layout.chat_message,
                    FirechatMsgViewHolder.class,
                    mSimpleFirechatDatabaseReference.child("messages")) {

                @Override
                protected void populateViewHolder(FirechatMsgViewHolder viewHolder, ChatMessage friendlyMessage, int position) {
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                    viewHolder.msgTextView.setText(friendlyMessage.getText());
                    viewHolder.userTextView.setText(friendlyMessage.getName());
                    if (friendlyMessage.getPhotoUrl() == null) {
                        viewHolder.userImageView
                                .setImageDrawable(ContextCompat
                                        .getDrawable(getActivity(),
                                                R.drawable.ic_account_circle_black_36dp));
                    } else {
                        Glide.with(getActivity())
                                .load(friendlyMessage.getPhotoUrl())
                                .into(viewHolder.userImageView);
                    }
                }
            };

            mMsgEditText = (EditText) rootView.findViewById(R.id.msgEditText);
            mSendButton = (Button) rootView.findViewById(R.id.sendButton);
            mSendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ChatMessage friendlyMessage = new
                            ChatMessage(mMsgEditText.getText().toString(),
                            mUsername,
                            mPhotoUrl);
                    mSimpleFirechatDatabaseReference.child("messages")
                            .push().setValue(friendlyMessage);
                    mMsgEditText.setText("");
                }
            });

            mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    int chatMessageCount = mFirebaseAdapter.getItemCount();
                    int lastVisiblePosition =
                            mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                    if (lastVisiblePosition == -1 ||
                            (positionStart >= (chatMessageCount - 1) &&
                                    lastVisiblePosition == (positionStart - 1))) {
                        mMessageRecyclerView.scrollToPosition(positionStart);
                    }
                }
            });

            mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
            mMessageRecyclerView.setAdapter(mFirebaseAdapter);

            return rootView;
        }
    }
    
**ChatMessage.java - класс для работы с сообщениями в чате**
public class ChatMessage {

        private String text;
        private String name;
        private String photoUrl;

        public ChatMessage() {
        }

        public ChatMessage(String text, String name, String photoUrl) {
            this.text = text;
            this.name = name;
            this.photoUrl = photoUrl;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhotoUrl() {
            return photoUrl;
        }

        public void setPhotoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
        }
    }
    
**DBHelper.java - класс отвечающий за создание SQLite базы данных(Подключается опционально)**
public class DBHelper extends SQLiteOpenHelper {

        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "ArcDB";
        public static final String TABLE_ORDER = "arcboxes";

        public static final String KEY_ID = "_id";
        public static final String KEY_NAME = "name";
        public static final String KEY_WEIGHT = "weight";
        public static final String KEY_FROM = "fromm";
        public static final String KEY_TO = "too";
        public static final String KEY_FIO = "fio";
        public static final String KEY_EMAIL = "email";
        public static final String KEY_PHONE = "phone";


        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table " + TABLE_ORDER + " (" + KEY_ID
                    + " integer primary key autoincrement," + KEY_NAME + " text," + KEY_WEIGHT + " text," + KEY_FROM
                    + " text," + KEY_TO + " text," + KEY_FIO + " text," + KEY_EMAIL + " text," + KEY_PHONE
                    + " text" + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(TABLE_ORDER);
            onCreate(db);
        }
    }

**FormActivity.java класс отвечающий за вкладку регистрации товара**

    public class FormActivity extends Fragment implements View.OnClickListener{

        DBHelper dbHelper;

        Button btnAdd, btnRead, btnClean, btnUpdate, btnDel;
        EditText edId, edName, edWeight, edFrom, edTo, edFIO, edEmail, edPhone;
        TextView tvWait;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.activity_form, container, false);


            btnAdd = (Button) rootView.findViewById(R.id.btnAdd);
            btnAdd.setOnClickListener(this);
            //btnAdd.setEnabled(true);
            /*btnRead = (Button) rootView.findViewById(R.id.btnRead);
            btnRead.setOnClickListener(this);
            btnClean = (Button) rootView.findViewById(R.id.btnClean);
            btnClean.setOnClickListener(this);
            btnUpdate = (Button) rootView.findViewById(R.id.btnUpdate);
            btnUpdate.setOnClickListener(this);
            btnDel = (Button) rootView.findViewById(R.id.btnDel);
            btnDel.setOnClickListener(this);*/

            //edId = (EditText) rootView.findViewById(R.id.edId);
            edName = (EditText) rootView.findViewById(R.id.edName);
            edWeight = (EditText) rootView.findViewById(R.id.edWeight);
            edFrom = (EditText) rootView.findViewById(R.id.edFrom);
            edTo = (EditText) rootView.findViewById(R.id.edTo);
            edFIO = (EditText) rootView.findViewById(R.id.edFIO);
            edEmail = (EditText) rootView.findViewById(R.id.edEmail);
            edPhone = (EditText) rootView.findViewById(R.id.edPhone);

            tvWait = (TextView) rootView.findViewById(R.id.tvWait);

            getActivity();
            dbHelper = new DBHelper(getActivity());

            return rootView;
        }

        @Override
        public void onClick(View v) {
            //String id = edId.getText().toString();
            String name = edName.getText().toString();
            String weight = edWeight.getText().toString();
            String from = edFrom.getText().toString();
            String to = edTo.getText().toString();
            String fio = edFIO.getText().toString();
            String email = edEmail.getText().toString();
            String phone = edPhone.getText().toString();

            SQLiteDatabase database = dbHelper.getWritableDatabase();
            //Add items
            ContentValues contentValues = new ContentValues();

            switch (v.getId()){
                case R.id.btnAdd:
                    if(name.equalsIgnoreCase("") || weight.equalsIgnoreCase("") || from.equalsIgnoreCase("")
                            || to.equalsIgnoreCase("") || fio.equalsIgnoreCase("") || email.equalsIgnoreCase("")
                            || phone.equalsIgnoreCase("")){
                        Toast.makeText(getActivity(), "Incorrect ", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    contentValues.put(DBHelper.KEY_NAME, name);
                    contentValues.put(DBHelper.KEY_WEIGHT, weight);
                    contentValues.put(DBHelper.KEY_FROM, from);
                    contentValues.put(DBHelper.KEY_TO, to);
                    contentValues.put(DBHelper.KEY_FIO, fio);
                    contentValues.put(DBHelper.KEY_EMAIL, email);
                    contentValues.put(DBHelper.KEY_PHONE, phone);
                    database.insert(DBHelper.TABLE_ORDER, null, contentValues);

                    FirebaseDatabase databases = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = databases.getReference("Order - " + edName.getText().toString());
                    myRef.setValue(weight + " "
                            + from + " "
                            + to + " "
                            + fio + " "
                            + email + " "
                            + phone + ".");
                    edName.setText(" ");
                    edWeight.setText(" ");
                    edFrom.setText(" ");
                    edTo.setText(" ");
                    edFIO.setText(" ");
                    edEmail.setText(" ");
                    edPhone.setText(" ");
                    tvWait.setText("Заказ оформлен! Ожидайте ответ от оператора.");
                    break;

                /*case R.id.btnRead:
                    Cursor cursor = database.query(DBHelper.TABLE_ORDER, null, null, null, null, null, null);
                    if(cursor.moveToFirst()){
                        int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
                        int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
                        int weightIndex = cursor.getColumnIndex(DBHelper.KEY_WEIGHT);
                        int fromIndex = cursor.getColumnIndex(DBHelper.KEY_FROM);
                        int toIndex = cursor.getColumnIndex(DBHelper.KEY_TO);
                        int fioIndex = cursor.getColumnIndex(DBHelper.KEY_FIO);
                        int emailIndex = cursor.getColumnIndex(DBHelper.KEY_EMAIL);
                        int phoneIndex = cursor.getColumnIndex(DBHelper.KEY_PHONE);
                        do {
                            Log.d("mLog", "ID = " + cursor.getInt(idIndex)
                                    + ", Description = " + cursor.getString(nameIndex)
                                    + ", Weight = " + cursor.getString(weightIndex)
                                    + ", From = " + cursor.getString(fromIndex)
                                    + ", To = " + cursor.getString(toIndex)
                                    + ", FIO = " + cursor.getString(fioIndex)
                                    + ", Email = " + cursor.getString(emailIndex)
                                    + ", Phone = " + cursor.getString(phoneIndex));
                        } while (cursor.moveToNext());
                    } else Log.d("mLog", "0 rows");
                    cursor.close();
                    break;
                case R.id.btnClean:
                    database.delete(DBHelper.TABLE_ORDER, null, null);
                    break;
                case R.id.btnUpdate:
                    if(id.equalsIgnoreCase("")){
                        Toast.makeText(getActivity(), "incorrect id", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    contentValues.put(DBHelper.KEY_NAME, name);
                    contentValues.put(DBHelper.KEY_WEIGHT, weight);
                    contentValues.put(DBHelper.KEY_FROM, from);
                    contentValues.put(DBHelper.KEY_TO, to);
                    contentValues.put(DBHelper.KEY_FIO, fio);
                    contentValues.put(DBHelper.KEY_EMAIL, email);
                    contentValues.put(DBHelper.KEY_PHONE, phone);
                    int updCount = database.update(DBHelper.TABLE_ORDER, contentValues, DBHelper.KEY_ID + "= ?", new String[] {id});
                    Log.d("mLog", "Updates rows count = " + updCount);
                    break;
                case R.id.btnDel:
                    if (id.equalsIgnoreCase("")){
                        Toast.makeText(getActivity(), "incorrect id", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    int delCount = database.delete(DBHelper.TABLE_ORDER, DBHelper.KEY_ID + "= ?", new String[] {id});
                    Log.d("mLog", "Deleted rows count = " + delCount);*/
            }

        }

    }
    
**MainActivity.java - класс отвечающий за создание вкладок**
public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{


        private SectionsPagerAdapter mSectionsPagerAdapter;
        private ViewPager mViewPager;

        private GoogleApiClient mGoogleApiClient;
        private FirebaseAuth mFirebaseAuth;
        private FirebaseUser mFirechatUser;

        private String mUsername;
        private String mPhotoUrl;

        private FirebaseRemoteConfig mFirebaseRemoteConfig;
        public static final String DEFAULT_NAME = "Arthur";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            // Create the adapter that will return a fragment for each of the three
            // primary sections of the activity.
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);

            //here mGoogleApiClient
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API)
                    .build();
            mFirebaseAuth = FirebaseAuth.getInstance();
            mFirechatUser = mFirebaseAuth.getCurrentUser();
            if (mFirechatUser == null) {
                startActivity(new Intent(this, AuthorizationActivity.class));
                //finish();
                //return;
            } else {
                mUsername = mFirechatUser.getDisplayName();
                if (mFirechatUser.getPhotoUrl() != null) {
                    mPhotoUrl = mFirechatUser.getPhotoUrl().toString();
                }
            }
        }


        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.sign_out_menu:
                    mFirebaseAuth.signOut();
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                    mUsername = DEFAULT_NAME;
                    startActivity(new Intent(this, AuthorizationActivity.class));
                    return true;
            }
            return super.onOptionsItemSelected(item);
        }



        //PlaceholderFragment was here
        /**
         * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
         * one of the sections/tabs/pages.
         */
        public class SectionsPagerAdapter extends FragmentPagerAdapter {

            public SectionsPagerAdapter(FragmentManager fm) {
                super(fm);
            }

            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        Tab1HowToUse tab1 = new Tab1HowToUse();
                        return tab1;
                    case 1:
                        FormActivity tab2 = new FormActivity();
                        return tab2;
                    case 2:
                        ChatActivity tab3 = new ChatActivity();
                        return tab3;
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                // Show 3 total pages.
                return 3;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return "How to use?";
                    case 1:
                        return "Form";
                    case 2:
                        return "Chat";
                }
                return null;
            }
        }


        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
        }

    }
    
**SimpleFirechatInstanceIdService.java и SimpleFirechatMessagingService.java - классы для отправки уведомлений на телефон**

    public class SimpleFirechatInstanceIdService extends FirebaseInstanceIdService {


        private static final String CHAT_ENGAGE_TOPIC = "chat_engage";

        public void onTokenRefresh() {
            String token = FirebaseInstanceId.getInstance().getToken();
            FirebaseMessaging.getInstance()
                    .subscribeToTopic(CHAT_ENGAGE_TOPIC);
        }

    }
    
    import com.google.firebase.messaging.FirebaseMessagingService;
    import com.google.firebase.messaging.RemoteMessage;

    public class SimpleFirechatMessagingService extends FirebaseMessagingService {



        public void onMessageReceived(RemoteMessage remoteMessage) {

        }

    }
    
**Tab1HowToUse.java - класс для создания окна с инструкциями**
public class Tab1HowToUse extends Fragment {

        TextView tvHowToUse;
        Firebase firebaseReference;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.activity_use, container, false);

            Firebase.setAndroidContext(getActivity());
            tvHowToUse = (TextView) rootView.findViewById(R.id.tvHowToUse);
            firebaseReference = new Firebase("https://arcboxv2.firebaseio.com/HowTo/type");

            firebaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String text = dataSnapshot.getValue(String.class);
                    tvHowToUse.setText(text);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });


            return rootView;
        }
    }
    
###Используемая литература/сайты.
"Effective Java - Joshua Bloch"

["habrahabr.ru"](habrahabr.ru)

["http://startandroid.ru/ru/"](http://startandroid.ru/ru/)

["stackoverflow.com"](stackoverflow.com)

["developers.google.com"](https://developers.google.com)
