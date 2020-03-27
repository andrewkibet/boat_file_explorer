package com.bomboverk.boat.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.UriPermission;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bomboverk.boat.Database.DatabaseHelper;
import com.bomboverk.boat.ExplorerActivity;
import com.bomboverk.boat.R;
import com.bomboverk.boat.StorageHelpers.StorageHelper;
import com.bomboverk.boat.SystemInformation.SystemInformationHelper;

import java.util.List;

public class HomeFragment extends Fragment {

    private StorageHelper storageHelper;
    private DatabaseHelper database;
    private SystemInformationHelper SIH;

    private ConstraintLayout layoutGeral;
    private FrameLayout externalLayout;

    private TextView progressBarRamText;
    private TextView progressBarInternalText;
    private TextView progressBarExternalText;

    private ProgressBar progressBarRam;
    private ProgressBar progressBarMemInternal;
    private ProgressBar progressBarMemExternal;

    private CardView btnInternal;
    private CardView btnExternal;

    private ImageView editInternal;
    private ImageView editExterno;

    private TextView internalWayText;
    private TextView externalWayText;

    //RAM INFOS
    private int totalRam = 0;
    private int usedRam = 0;

    //INTERNAL INFOS
    private int totalIternalSize = 0;
    private int usedInternalSize = 0;

    private boolean updateWays = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View fragment = inflater.inflate(R.layout.fragment_home, container, false);

        layoutGeral = fragment.findViewById(R.id.general_Layout);
        externalLayout = fragment.findViewById(R.id.frameLExternal);

        progressBarRamText = fragment.findViewById(R.id.textTitlePgbRam);
        progressBarInternalText = fragment.findViewById(R.id.textTitlePgbDiskInternal);
        progressBarExternalText = fragment.findViewById(R.id.textTitlePgbDiskExternal);

        progressBarRam = fragment.findViewById(R.id.progressBar_Ram);
        progressBarMemInternal = fragment.findViewById(R.id.progressBar_DiskInternal);
        progressBarMemExternal = fragment.findViewById(R.id.progressBar_DiskExternal);

        btnInternal = fragment.findViewById(R.id.cardInternal);
        btnExternal = fragment.findViewById(R.id.cardExternal);

        editInternal = fragment.findViewById(R.id.frag_home_edit_internal);
        editExterno = fragment.findViewById(R.id.frag_home_edit_external);

        internalWayText = fragment.findViewById(R.id.frag_home_internalway);
        externalWayText = fragment.findViewById(R.id.frag_home_externalway);

        database = new DatabaseHelper(getActivity());
        SIH = new SystemInformationHelper(getActivity());
        storageHelper = new StorageHelper(null, getActivity());

        if (checkWays("internal")) {
            loadBars();
        } else {
            database.deleteWays("internal");
            createInfoWays(getString(R.string.frag_home_infoways_title_internal), getString(R.string.frag_home_infoways_message_internal), "internal");
        }

        return fragment;
    }

    private boolean checkWays(String way) {
        if (way.equals("internal")) {
            if (checkAccess(database.getWays("internal"))) {
                return true;
            } else {
                return false;
            }
        } else {
            if (checkAccess(database.getWays("external"))) {
                return true;
            } else {
                return false;
            }
        }
    }

    private boolean checkAccess(String path) {
        List<UriPermission> permissionList = getUriPermissionsList();

        for (int i = 0; i < permissionList.size(); i++) {
            if (permissionList.get(i).getUri().toString().equals(path) && permissionList.get(i).isWritePermission()) {
                return true;
            }
        }
        return false;
    }

    public void openToSelectStorage(String storage) {
        if (storage.equals("internal")) {
            startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), 1);
        } else {
            startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), 2);
        }
    }

    private List<UriPermission> getUriPermissionsList() {
        return getActivity().getContentResolver().getPersistedUriPermissions();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        switch (requestCode) {
            case 1:
                if (resultCode != Activity.RESULT_OK) {
                    createInfoWays(getString(R.string.general_07), getString(R.string.frag_home_infoways_errormessage_internal), "internal");
                } else {
                    Uri treeUri = resultData.getData();

                    getActivity().grantUriPermission(getActivity().getPackageName(), treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    getActivity().getContentResolver().takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    if (!updateWays) {
                        database.insertWay("internal", treeUri.toString());
                    } else {
                        database.updateWays("internal", treeUri.toString());
                    }

                    if (checkWays("internal")) {
                        loadBars();
                    }
                }
                break;
            case 2:
                if (resultCode != Activity.RESULT_OK) {
                    createInfoWays(getString(R.string.general_07), getString(R.string.frag_home_infoways_errormessage_external), "external");
                } else {
                    Uri treeUri = resultData.getData();

                    getActivity().grantUriPermission(getActivity().getPackageName(), treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    getActivity().getContentResolver().takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);


                    if (!updateWays){
                        database.insertWay("external", treeUri.toString());
                    }else{
                        database.updateWays("external", treeUri.toString());
                    }

                    if (checkWays("external")) {
                        loadBars();
                        openSdCard();
                    }
                }
                break;
        }
    }

    private void loadBars() {

        layoutGeral.setVisibility(View.VISIBLE);
        //internalWayText.setText(formatWay(database.getWays("internal")));
        internalWayText.setText(storageHelper.formatWay(database.getWays("internal")));

        totalRam = SIH.getTotalRamMemorySize();
        usedRam = SIH.getUsedRamMemorySize();

        totalIternalSize = (int) SIH.getTotalMemorySize(database.getWays("internal"));
        usedInternalSize = (int) SIH.getUsedMemorySize(database.getWays("internal"));

        progressBarRam.setMax(totalRam);
        progressBarRam.setProgress(usedRam);

        int percentageRam = (int) calculatePercentage(usedRam, totalRam);
        progressBarRamText.setText(getString(R.string.frag_home_ram_usage) + "(" + percentageRam + "%)");

        progressBarMemInternal.setMax(totalIternalSize);
        progressBarMemInternal.setProgress(usedInternalSize);

        int percentageInternal = (int) calculatePercentage(usedInternalSize, totalIternalSize);
        progressBarInternalText.setText(getString(R.string.frag_home_internal_usage) + "(" + percentageInternal + "%)");

        if (!database.getWays("external").equals("noneway")) {
            progressBarMemExternal.setMax((int) SIH.getTotalMemorySize(database.getWays("external")));
            progressBarMemExternal.setProgress((int) SIH.getUsedMemorySize(database.getWays("external")));

            int percentageExternal = (int) calculatePercentage(SIH.getUsedMemorySize(database.getWays("external")), SIH.getTotalMemorySize(database.getWays("external")));
            progressBarExternalText.setText(getString(R.string.frag_home_external_usage) + "(" + percentageExternal + "%)");

            //externalWayText.setText(formatWay(database.getWays("external")));
            externalWayText.setText(storageHelper.formatWay(database.getWays("external")));
            externalLayout.setVisibility(View.VISIBLE);
        } else {
            externalWayText.setText(getString(R.string.frag_home_noways));
        }

        buttonsEvents();
    }

    private void buttonsEvents() {
        btnInternal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ExplorerActivity.class);
                intent.putExtra("url", database.getWays("internal"));
                intent.putExtra("type", "notype");
                startActivity(intent);
            }
        });

        btnExternal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSdCard();
            }
        });

        editInternal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateWays = true;
                createInfoWays(getString(R.string.frag_home_infoways_title_internal), getString(R.string.frag_home_infoways_message_newinternal), "internal");
            }
        });

        editExterno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateWays = true;
                createInfoWays(getString(R.string.frag_home_infoways_title_external), getString(R.string.frag_home_infoways_message_newexternal), "external");
            }
        });
    }

    private void openSdCard() {
        if (checkWays("external")) {
            Intent intent = new Intent(getActivity(), ExplorerActivity.class);
            intent.putExtra("url", database.getWays("external"));
            intent.putExtra("type", "notype");
            startActivity(intent);
        } else {
            database.deleteWays("external");
            createInfoWays(getString(R.string.frag_home_infoways_title_external), getString(R.string.frag_home_infoways_message_external), "external");
        }
    }

    public double calculatePercentage(double obtained, double total) {
        return obtained * 100 / total;
    }

    private void createInfoWays(String titulo, String mensagem, final String way){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(mensagem)
                .setTitle(titulo)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        openToSelectStorage(way);
                    }
                });
        builder.create();
        builder.show();
    }
}
