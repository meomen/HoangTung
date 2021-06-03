package firebase.gopool.Chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import firebase.gopool.Common.Common;
import firebase.gopool.Common.CommonAgr;
import firebase.gopool.R;
import firebase.gopool.Utils.FirebaseMethods;
import firebase.gopool.models.ChatInfoModel;
import firebase.gopool.models.ChatMessageModel;
import firebase.gopool.models.User;

public class ChatDetailActivity extends AppCompatActivity implements ILoadTimeFromFirebaseListener, View.OnClickListener {
    public static final int MY_CAMERA_REQUEST_CODE = 1999;
    public static final int MY_RESULT_LOAD_IMG = 6999;

    private String roomId,chatSender,idCurrent, idPartner;

    Toolbar toolbar;
    ImageView img_camera, img_image, img_send, img_preview;
    AppCompatEditText edt_chat;
    RecyclerView recycler_chat;

    private LinearLayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference chatRef,offsetRef;
    ILoadTimeFromFirebaseListener listener;

    FirebaseRecyclerAdapter<ChatMessageModel,RecyclerView.ViewHolder> adapter;
    FirebaseRecyclerOptions<ChatMessageModel> options;

    Uri fileUri;
    User currentUser;
    StorageReference storageReference;
    private FirebaseMethods mFirebaseMethods;
    private FirebaseDatabase mFirebaseDatabse;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_details);

        init();
        initViews();
        loadChatContent();
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        img_camera = (ImageView) findViewById(R.id.img_camera);
        img_image = (ImageView) findViewById(R.id.img_image);
        img_send = (ImageView) findViewById(R.id.img_send);
        img_preview = (ImageView) findViewById(R.id.img_preview);
        edt_chat = (AppCompatEditText) findViewById(R.id.edt_chat);
        recycler_chat = (RecyclerView) findViewById(R.id.recycler_chat);

        img_image.setOnClickListener(this);
        img_camera.setOnClickListener(this);
        img_send.setOnClickListener(this);

        mFirebaseMethods = new FirebaseMethods(this);
        mFirebaseDatabse = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabse.getReference();

        getCurrentUser();
    }
    private void initViews() {

        idCurrent = getIntent().getStringExtra(CommonAgr.ID_CURRENT);
        idPartner = getIntent().getStringExtra(CommonAgr.ID_PARTNER);
        chatSender = getIntent().getStringExtra(CommonAgr.KEY_CHAT_USER);

        listener = this;
        database = FirebaseDatabase.getInstance();
        chatRef = database.getReference(CommonAgr.CHAT_REF);
        offsetRef = database.getReference(".info/serverTimeOffset");
        checkRoomId();

        Query query = chatRef.child(roomId)
                .child(CommonAgr.CHAT_DETAIL_REF);

        options = new FirebaseRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query,ChatMessageModel.class)
                .build();
        layoutManager= new LinearLayoutManager(this);
        recycler_chat.setLayoutManager(layoutManager);

        toolbar.setTitle(chatSender);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_image: {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,MY_RESULT_LOAD_IMG);
                break;
            }
            case R.id.img_camera: {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                fileUri = getOutputMediaFileUri();
                intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
                startActivityForResult(intent,MY_CAMERA_REQUEST_CODE);
                break;
            }
            case R.id.img_send: {
                offsetRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long offset = snapshot.getValue(Long.class);
                        long estimatedServerTimeInMs = System.currentTimeMillis() + offset;

                        listener.onLoadOnlyTimeSuccess(estimatedServerTimeInMs);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.onLoadtimeFailed(error.getMessage());
                    }
                });
                break;
            }
        }
    }
    public void getCurrentUser() {
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //retrieve user information from the database
                currentUser = mFirebaseMethods.getUserSettings(dataSnapshot);
                currentUser.setUser_id(Common.userID);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onLoadOnlyTimeSuccess(long estimateTimeInMs) {
        ChatMessageModel chatMessageModel = new ChatMessageModel();
        chatMessageModel.setName(currentUser.getUsername());
        chatMessageModel.setContent(edt_chat.getText().toString());
        chatMessageModel.setTimeStamp(estimateTimeInMs);
        chatMessageModel.setOwnId(currentUser.getUser_id());

        if(fileUri == null) {
            chatMessageModel.setPicture(false);
            submitChatToFirebase(chatMessageModel,chatMessageModel.isPicture(),estimateTimeInMs);
        }
        else
            uploadPicture(fileUri,chatMessageModel,estimateTimeInMs);
    }

    @Override
    public void onLoadtimeFailed(String message) {

    }
    private Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }
    private File getOutputMediaFile() {
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "NiceFoodServer");

        if(!mediaStorageDir.exists()) {
            if(!mediaStorageDir.mkdir())
                return null;
        }

        String time_stamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile = new File(new StringBuilder(mediaStorageDir.getPath())
                .append(File.separator)
                .append("IMG_")
                .append(time_stamp)
                .append("_")
                .append(new Random().nextInt())
                .append(".jpg")
                .toString());
        return mediaFile;
    }

    private void loadChatContent() {
        adapter = new FirebaseRecyclerAdapter<ChatMessageModel, RecyclerView.ViewHolder>(options) {

            @Override
            public int getItemViewType(int position) {
//                return adapter.getItem(position).isPicture() ? 1 : 0;
                ChatMessageModel chatMessageModel = adapter.getItem(position);


                if(!chatMessageModel.getOwnId().equals(currentUser.getUser_id())    // Tin nhắn khách, không có hình
                        && !chatMessageModel.isPicture()) {
                    return 0;
                }
                else if(!chatMessageModel.getOwnId().equals(currentUser.getUser_id())   // Tin nhắn khách, có hình
                        && chatMessageModel.isPicture()) {
                    return 1;
                }
                else if(chatMessageModel.getOwnId().equals(currentUser.getUser_id())   // Tin nhắn của mình, không có hình
                        && !chatMessageModel.isPicture()) {
                    return 2;
                }
                else if(chatMessageModel.getOwnId().equals(currentUser.getUser_id())    // Tin nhắn của mình, có hình
                        && chatMessageModel.isPicture()) {
                    return 3;
                }
                else {
                    return 0;   // Default
                }
            }

            @Override
            protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull ChatMessageModel model) {
                if(holder instanceof ChatTextHolder) {
                    ChatTextHolder chatTextHolder = (ChatTextHolder) holder;
                    chatTextHolder.tv_email.setText(model.getName());
                    chatTextHolder.tv_chat_message.setText(model.getContent());
                    chatTextHolder.tv_time.setText(
                            DateUtils.getRelativeTimeSpanString(model.getTimeStamp(),
                                    Calendar.getInstance().getTimeInMillis(),0).toString());
                }
                else {
                    ChatPictureHolder chatPictureHolder = (ChatPictureHolder) holder;
                    chatPictureHolder.tv_email.setText(model.getName());
                    chatPictureHolder.tv_chat_message.setText(model.getContent());
                    chatPictureHolder.tv_time.setText(
                            DateUtils.getRelativeTimeSpanString(model.getTimeStamp(),
                                    Calendar.getInstance().getTimeInMillis(),0).toString());

                    Glide.with(ChatDetailActivity.this)
                            .load(model.getPictureLink())
                            .into(chatPictureHolder.img_preview);
                }
            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view;
                if(viewType == 0) {
                    view = LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.layout_message_text,viewGroup,false);
                    return new ChatTextHolder(view);
                }
                else if(viewType == 1) {
                    view = LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.layout_message_picture,viewGroup,false);
                    return new ChatPictureHolder(view);
                }
                else if(viewType == 2) {
                    view = LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.layout_my_message_text,viewGroup,false);
                    return new ChatTextHolder(view);
                }
                else if(viewType == 3) {
                    view = LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.layout_my_message_picture,viewGroup,false);
                    return new ChatPictureHolder(view);
                }
                else {
                    view = LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.layout_message_text,viewGroup,false);
                    return new ChatTextHolder(view);
                }
            }
        };
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = adapter.getItemCount();
                int latsVisiablepostion = layoutManager.findLastCompletelyVisibleItemPosition();
                if(latsVisiablepostion == -1 ||
                        (positionStart >= (friendlyMessageCount-1) &&
                                latsVisiablepostion == (positionStart-1))) {
                    recycler_chat.scrollToPosition(positionStart);
                }
            }
        });

        recycler_chat.setAdapter(adapter);
    }

    private void submitChatToFirebase(ChatMessageModel chatMessageModel, boolean isPicture,long estimateTimeInMs) {
        chatRef.child(roomId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            appendChat(chatMessageModel,isPicture,estimateTimeInMs);
                        }
                        else {
                            createChat(chatMessageModel,isPicture,estimateTimeInMs);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ChatDetailActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void appendChat(ChatMessageModel chatMessageModel, boolean isPicture, long estimateTimeInMs) {
        Map<String,Object> update_data = new HashMap<>();
        update_data.put("lastUpdate",estimateTimeInMs);
        if(isPicture)
            update_data.put("lastMessage","<Image>");
        else
            update_data.put("lastMessage",chatMessageModel.getContent());

        chatRef.child(roomId)
                .updateChildren(update_data)
                .addOnFailureListener(e -> {
                    Toast.makeText(ChatDetailActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                })
                .addOnCompleteListener(task2 -> {
                    if(task2.isSuccessful()) {
                        chatRef.child(roomId)
                                .child(CommonAgr.CHAT_DETAIL_REF)
                                .push()
                                .setValue(chatMessageModel)
                                .addOnFailureListener(e -> {
                                    Toast.makeText(ChatDetailActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                                }).addOnCompleteListener(task -> {
                            if(task.isSuccessful()) {
                                edt_chat.setText("");
                                edt_chat.requestFocus();
                                if(adapter != null) {
                                    adapter.notifyDataSetChanged();
                                    if(isPicture) {
                                        fileUri = null;
                                        img_preview.setVisibility(View.GONE);
                                    }
                                }
                            }
                        });
                    }
                });
    }

    private void createChat(ChatMessageModel chatMessageModel, boolean isPicture, long estimateTimeInMs) {
        ChatInfoModel chatInfoModel = new ChatInfoModel();
        chatInfoModel.setCreateaName(chatMessageModel.getName());
        if(isPicture)
            chatInfoModel.setLastMessage("<Image>");
        else
            chatInfoModel.setLastMessage(chatMessageModel.getContent());
        chatInfoModel.setLastUpdate(estimateTimeInMs);
        chatInfoModel.setCreateDate(estimateTimeInMs);

        chatRef.child(roomId)
                .setValue(chatInfoModel)
                .addOnFailureListener(e -> Toast.makeText(ChatDetailActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show())
                .addOnCompleteListener(task2 -> {
                    if(task2.isSuccessful()) {
                        chatRef.child(roomId)
                                .child(CommonAgr.CHAT_DETAIL_REF)
                                .push()
                                .setValue(chatMessageModel)
                                .addOnFailureListener(e -> {
                                    Toast.makeText(ChatDetailActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                                }).addOnCompleteListener(task -> {
                            if(task.isSuccessful()) {
                                edt_chat.setText("");
                                edt_chat.requestFocus();
                                if(adapter != null) {
                                    adapter.notifyDataSetChanged();
                                    if(isPicture) {
                                        fileUri = null;
                                        img_preview.setVisibility(View.GONE);
                                    }
                                }
                            }
                        });
                    }
                });
    }
    private void uploadPicture(Uri fileUri, ChatMessageModel chatMessageModel,long estimateTimeInMs) {
        if(fileUri != null) {
            AlertDialog dialog = new AlertDialog.Builder(ChatDetailActivity.this)
                    .setCancelable(false)
                    .setMessage("Please wait...")
                    .create();
            dialog.show();

            String fileName = Common.getFileName(getContentResolver(),fileUri);
            String path = new StringBuilder()
                    .append("/")
                    .append(fileName)
                    .toString();
            storageReference = FirebaseStorage.getInstance().getReference(path);

            UploadTask uploadTask = storageReference.putFile(fileUri);

            //Create task
            Task<Uri> task = uploadTask.continueWithTask(task1 -> {
                return storageReference.getDownloadUrl();
            }).addOnCompleteListener(task12 -> {
                if(task12.isSuccessful()) {
                    String url = task12.getResult().toString();
                    dialog.dismiss();

                    chatMessageModel.setPicture(true);
                    chatMessageModel.setPictureLink(url);

                    submitChatToFirebase(chatMessageModel,chatMessageModel.isPicture(),estimateTimeInMs);
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
            });

        }
        else {
            Toast.makeText(this,"Image is empty",Toast.LENGTH_SHORT).show();
        }
    }

    private void checkRoomId() {
        final boolean[] isExists = {false};
        String room = idCurrent + "_"+ idPartner;
        String temp1 = room;
        chatRef.child(room)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            isExists[0] = true;
                            roomId = temp1;
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ChatDetailActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        if (isExists[0]) return;

        room = idPartner + "_"+ idCurrent;
        String temp2 = room;
        chatRef.child(room)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            isExists[0] = true;
                            roomId = temp2;
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ChatDetailActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        if (isExists[0] == false) roomId = idCurrent + "_"+ idPartner;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == MY_CAMERA_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                Bitmap bitmap = null;
                ExifInterface ei= null;
                try{
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),fileUri);
                    ei = new ExifInterface(getContentResolver().openInputStream(fileUri));

                    int orientation = ei.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED
                    );
                    Bitmap rotateBitmap = null;
                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotateBitmap = rotateBitmap(bitmap,90);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotateBitmap = rotateBitmap(bitmap,180);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotateBitmap = rotateBitmap(bitmap,270);
                            break;
                        default:
                            rotateBitmap = bitmap;
                            break;
                    }
                    img_preview.setVisibility(View.VISIBLE);
                    img_preview.setImageBitmap(rotateBitmap);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else if(requestCode == MY_RESULT_LOAD_IMG) {
            if(resultCode == RESULT_OK) {
                try {
                    final Uri imageUri = data.getData();
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    Bitmap selectedImage = BitmapFactory.decodeStream(inputStream);
                    img_preview.setImageBitmap(selectedImage);
                    img_preview.setVisibility(View.VISIBLE);
                    fileUri = imageUri;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this,"File not found",Toast.LENGTH_SHORT).show();
                }
            }
            else
                Toast.makeText(this,"Please choose image",Toast.LENGTH_SHORT).show();
        }
    }
    private Bitmap rotateBitmap(Bitmap bitmap,int i) {
        Matrix matrix = new Matrix();
        matrix.postRotate(i);
        return Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),
                bitmap.getHeight(),
                matrix,true);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if(adapter != null)
            adapter.startListening();
    }

    @Override
    protected void onStop() {
        if(adapter != null)
            adapter.stopListening();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter != null)
            adapter.startListening();
    }
}