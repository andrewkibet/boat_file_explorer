package com.bomboverk.boat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.bomboverk.boat.Dialogs.Dialogs;
import com.bomboverk.boat.ItensAdapter.Itens;
import com.bomboverk.boat.ItensAdapter.ItensAdaptador;
import com.bomboverk.boat.ItensAdapter.ItensViewHolder;
import com.bomboverk.boat.StorageHelpers.StorageHelper;
import com.bomboverk.boat.SystemInformation.SystemInformationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class ExplorerActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private StorageHelper stHelper;
    private SystemInformationHelper sysHelper;

    private Toolbar mToolbar;
    private BottomNavigationView bottomNav;
    private TextView folderWay;
    private ProgressBar progressBarWayFrame;

    private RecyclerView mRecyclerItens;
    private ArrayList<Itens> itens;
    private ItensAdaptador adaptador;

    private MenuItem renameItem, cutItem, navCopyItem, newFFItem, copyItem, cancelCopy, searchView, newFile, compactFile;

    private String urlSelected = "";
    private Dialogs dialogs;
    private String[] suportedExtension = {"zip", "rar", "txt", "pdf", "jpeg", "png", "jpg", "gif", "mp3", "wav", "mp4", "avi", "mov", "wmv", "mkv", "apk", "doc", "docm", "docx", "xltm", "xls", "xlt", "pptx", "pptm", "ppt"};
    private DocumentFile lastFile;

    loadItensAsync loaditens;
    loadFAItensAsync loadFAItensAsync;

    private boolean copyFileAndFolders = false;
    private String typeData = "notype";
    private FrameLayout waysFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer);

        mToolbar = findViewById(R.id.ex_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Boat - File Explorer");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bottomNav = findViewById(R.id.ex_bottom_toolbar);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        renameItem = bottomNav.getMenu().findItem(R.id.menu_nav_rename);
        cutItem = bottomNav.getMenu().findItem(R.id.menu_nav_cut);
        navCopyItem = bottomNav.getMenu().findItem(R.id.menu_nav_copy);

        folderWay = findViewById(R.id.ex_text_way);
        progressBarWayFrame = findViewById(R.id.act_ex_progress_load);
        waysFrame = findViewById(R.id.frameLayout_ways);

        stHelper = new StorageHelper(null, this);
        sysHelper = new SystemInformationHelper(this);
        dialogs = new Dialogs(this);

        mRecyclerItens = findViewById(R.id.ex_recycleview);

        itens = new ArrayList<>();

        Intent intent = this.getIntent();
        typeData = intent.getStringExtra("type");

        if (!typeData.equals("notype")) {
            navCopyItem.setVisible(false);
            cutItem.setVisible(false);
            renameItem.setVisible(false);
            getDatas();
        } else {
            urlSelected = intent.getStringExtra("url");
            DocumentFile pickedDir = DocumentFile.fromTreeUri(this, Uri.parse(urlSelected));
            loadItens(pickedDir);
        }
    }

    public void loadItens(DocumentFile doc) {
        loadDataRecycle();
        progressBarWayFrame.setVisibility(View.VISIBLE);
        String docId = DocumentsContract.getDocumentId(doc.getUri());
        docId = docId.replaceAll(":", "/");
        folderWay.setText(docId);

        lastFile = doc;

        if (loaditens != null && loaditens.getStatus() == AsyncTask.Status.RUNNING) {
            loaditens.cancel(true);
        }

        loaditens = new loadItensAsync();
        loaditens.execute(doc);
    }

    private void loadDataRecycle() {

        adaptador = new ItensAdaptador(itens, this, new ItensAdaptador.OnItemClickListener() {
            @Override
            public void onItemClick(Itens item, ItensViewHolder vholder, int position) {

                if (adaptador.selectedItems.size() > 0) {

                    if (adaptador.selectedItems.get(position, false)) {
                        adaptador.selectedItems.delete(position);
                        vholder.background.setSelected(false);
                    } else {
                        adaptador.selectedItems.put(position, true);
                        vholder.background.setSelected(true);
                    }

                    if (adaptador.selectedItems.size() == 0) {
                        prepareToSelect(false);
                    } else if (adaptador.selectedItems.size() > 1 && renameItem.isEnabled()) {
                        renameItem.setEnabled(false);
                    } else if (adaptador.selectedItems.size() == 1 && !renameItem.isEnabled()) {
                        renameItem.setEnabled(true);
                    }

                } else {

                    if (item.getDoc().isDirectory()) {
                        clearRecycle(item.getDoc());
                    } else {
                        String mime = item.getDoc().getType();

                        if (!mime.equals("application/octet-stream")){
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setDataAndType(item.getDoc().getUri(), mime);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(intent);
                        }else{
                            createSnack(getString(R.string.act_ex_snack_nosupported));
                        }
                    }
                }
            }

            @Override
            public void OnLongPress(Itens item, ItensViewHolder vholder, int position) {
                if (!adaptador.selectedItems.get(position, false)) {
                    if (adaptador.selectedItems.size() == 0) {
                        prepareToSelect(true);
                        adaptador.selectedItems.put(position, true);
                        vholder.background.setSelected(true);
                    } else if (adaptador.selectedItems.size() > 1 && renameItem.isEnabled()) {
                        renameItem.setEnabled(false);
                    } else if (adaptador.selectedItems.size() == 1 && !renameItem.isEnabled()) {
                        renameItem.setEnabled(true);
                    }
                }
            }
        });

        mRecyclerItens.setAdapter(adaptador);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerItens.setLayoutManager(layoutManager);
    }

    public void clearRecycle(DocumentFile document) {
        prepareToSelect(false);

        if (!copyItem.isVisible() && typeData.equals("notype")) {
            renameItem.setEnabled(true);
        }

        adaptador.selectedItems.clear();
        itens.clear();
        adaptador.notifyDataSetChanged();

        if (typeData.equals("notype")) {
            loadItens(document);
        } else {
            getDatas();
        }
    }

    private void prepareToSelect(boolean removeMode) {
        if (removeMode) {
            bottomNav.setVisibility(View.VISIBLE);
            if (typeData.equals("notype")) {
                compactFile.setVisible(true);
            }
        } else {
            compactFile.setVisible(false);
            bottomNav.setVisibility(View.GONE);
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.menu_nav_rename:
                    dialogs.createRename(adaptador.selectedItems, itens);
                    break;
                case R.id.menu_nav_copy:
                    copyFileAndFolders = true;
                    prepareToCopy(true);
                    break;
                case R.id.menu_nav_cut:
                    copyFileAndFolders = false;
                    prepareToCopy(true);
                    break;
                case R.id.menu_nav_delete:
                    dialogs.createDelete(getString(R.string.general_05), getString(R.string.act_ex_deletediag_part1) + " " + adaptador.selectedItems.size() + " " + getString(R.string.act_ex_deletediag_part2), adaptador.selectedItems, itens);
                    break;
            }
            return true;
        }
    };

    public void prepareToCopy(boolean action) {
        searchView.setVisible(!searchView.isVisible());
        newFile.setVisible(!newFile.isVisible());
        copyItem.setVisible(!copyItem.isVisible());
        cancelCopy.setVisible(!cancelCopy.isVisible());

        if (action) {
            dialogs.setSelectedItens(adaptador.selectedItems);
            dialogs.setItens(itens);

            prepareToSelect(false);
            adaptador.selectedItems.clear();
            adaptador.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_explorer, menu);

        newFFItem = menu.findItem(R.id.menu_item_novo);
        copyItem = menu.findItem(R.id.menu_item_copyFile);
        cancelCopy = menu.findItem(R.id.menu_item_cancelCopy);
        searchView = menu.findItem(R.id.menu_item_search);
        newFile = menu.findItem(R.id.menu_item_novo);
        compactFile = menu.findItem(R.id.menu_item_compact);

        SearchView searchViewObj = (SearchView) searchView.getActionView();
        searchViewObj.setQueryHint(getString(R.string.act_ex_search_hint));
        searchViewObj.setOnQueryTextListener(this);

        if (!typeData.equals("notype")) {
            newFFItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_item_novo:
                dialogs.createNewFile();
                break;
            case R.id.menu_item_copyFile:
                if (copyFileAndFolders) {
                    dialogs.createCopy(getString(R.string.general_06), getString(R.string.act_ex_copy_part1) + " " + dialogs.getSelectedItens().size() + " " + getString(R.string.act_ex_copy_part2));
                } else {
                    dialogs.createCut(getString(R.string.general_08), getString(R.string.act_ex_cutout_part1) + " " + dialogs.getSelectedItens().size() + " " + getString(R.string.act_ex_copy_part2));
                }
                break;
            case R.id.menu_item_cancelCopy:
                prepareToCopy(false);
                break;
            case R.id.menu_item_compact:
                dialogs.setSelectedItens(adaptador.selectedItems);
                dialogs.setItens(itens);

                dialogs.createCompact();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        String userInput = newText.toLowerCase();
        ArrayList<Itens> newList = new ArrayList<>();

        for (Itens m : itens) {
            if (m.getNome().toLowerCase().contains(userInput)) {
                newList.add(m);
            }
        }

        adaptador.updateList(newList);

        return false;
    }

    @Override
    public void onBackPressed() {
        verifyBack();
    }

    private void verifyBack() {
        if (typeData.equals("notype") && lastFile.getParentFile() != null) {
            clearRecycle(lastFile.getParentFile());
        } else {
            if (!typeData.equals("notype")) {
                loadFAItensAsync.cancel(true);
            }
            finish();
        }
    }

    public DocumentFile getLastFile() {
        return lastFile;
    }

    public void getDatas() {
        loadDataRecycle();
        waysFrame.setVisibility(View.GONE);
        loadFAItensAsync = new loadFAItensAsync();

        if (typeData.equals("sounds")) {
            loadFAItensAsync.execute(sysHelper.getSongs());
        } else if (typeData.equals("videos")) {
            loadFAItensAsync.execute(sysHelper.getVideos());
        } else if (typeData.equals("images")) {
            loadFAItensAsync.execute(sysHelper.getImages());
        }
    }

    public void createSnack(String text){
        Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_SHORT).setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }).setActionTextColor(getResources().getColor(R.color.colorPrimary)).show();
    }

    public String getTypeData() {
        return typeData;
    }

    private class loadFAItensAsync extends AsyncTask<ArrayList<Itens>, Itens, Itens> {

        @Override
        protected void onProgressUpdate(Itens... values) {
            if (!isCancelled()) {
                itens.add(values[0]);
                adaptador.notifyDataSetChanged();
            }
            super.onProgressUpdate(values);
        }

        @Override
        protected Itens doInBackground(ArrayList<Itens>... arrayLists) {

            for (Itens itemGet : arrayLists[0]) {
                if (!isCancelled()) {

                    String extension = stHelper.getExt(itemGet.getNome());
                    boolean supported = false;

                    if (extension != null && !extension.equals("")) {
                        for (String e : suportedExtension) {
                            if (extension.contains(e)) {
                                supported = true;
                                DocumentFile doc = DocumentFile.fromSingleUri(getApplicationContext(), Uri.parse(itemGet.getUrl()));
                                publishProgress(new Itens(doc.getName(), doc.getUri().toString(), e, stHelper.getFolderSizeLabel(doc), 0, doc));
                                break;
                            }
                        }
                        if (!supported) {
                            DocumentFile doc = DocumentFile.fromSingleUri(getApplicationContext(), Uri.parse(itemGet.getUrl()));
                            publishProgress(new Itens(doc.getName(), doc.getUri().toString(), extension, stHelper.getFolderSizeLabel(doc), 0, doc));
                        }
                    } else {
                        DocumentFile doc = DocumentFile.fromSingleUri(getApplicationContext(), Uri.parse(itemGet.getUrl()));
                        publishProgress(new Itens(doc.getName(), doc.getUri().toString(), "none", stHelper.getFolderSizeLabel(doc), 0, doc));
                    }
                }
            }

            return null;
        }
    }

    private class loadItensAsync extends AsyncTask<DocumentFile, Itens, Itens> {

        @Override
        protected Itens doInBackground(DocumentFile... documentFiles) {

            for (DocumentFile file : documentFiles[0].listFiles()) {
                if (!isCancelled()) {
                    if (file.isDirectory()) {
                        publishProgress(new Itens(file.getName(), file.getUri().toString(), "folder", "folder", file.listFiles().length, file));
                    } else {
                        String extension = stHelper.getExt(file.getName());
                        boolean supported = false;

                        if (extension != null && !extension.equals("")) {
                            for (String e : suportedExtension) {
                                if (extension.contains(e)) {
                                    supported = true;
                                    publishProgress(new Itens(file.getName(), file.getUri().toString(), e, stHelper.getFolderSizeLabel(file), 0, file));
                                    break;
                                }
                            }
                            if (!supported) {
                                publishProgress(new Itens(file.getName(), file.getUri().toString(), extension, stHelper.getFolderSizeLabel(file), 0, file));
                            }
                        } else {
                            publishProgress(new Itens(file.getName(), file.getUri().toString(), "none", stHelper.getFolderSizeLabel(file), 0, file));
                        }
                    }
                } else {
                    break;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Itens itens) {
            progressBarWayFrame.setVisibility(View.GONE);
            super.onPostExecute(itens);
        }

        @Override
        protected void onProgressUpdate(Itens... values) {
            if (!isCancelled()) {
                itens.add(values[0]);
                adaptador.notifyDataSetChanged();
            }
            super.onProgressUpdate(values);
        }
    }

}