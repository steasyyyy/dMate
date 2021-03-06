package de.dmate.marvin.dmate.fragments.SettingsDialogFragments;

import android.Manifest;
import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import de.dmate.marvin.dmate.R;
import de.dmate.marvin.dmate.roomDatabase.DataViewModel;
import de.dmate.marvin.dmate.roomDatabase.Entities.Daytime;
import de.dmate.marvin.dmate.roomDatabase.Entities.Entry;
import de.dmate.marvin.dmate.roomDatabase.Entities.Exercise;
import de.dmate.marvin.dmate.roomDatabase.Entities.Notification;
import de.dmate.marvin.dmate.roomDatabase.Entities.Observation;
import de.dmate.marvin.dmate.roomDatabase.Entities.PlannedBasalInjection;
import de.dmate.marvin.dmate.roomDatabase.Entities.Sport;
import de.dmate.marvin.dmate.roomDatabase.Entities.User;
import de.dmate.marvin.dmate.util.Helper;

public class ExportDialogFragment extends DialogFragment {

    private OnExportDialogFragmentInteractionListener mListener;

    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    private Boolean userAdded = false;
    private Boolean daytimesAdded = false;
    private Boolean sportsAdded = false;
    private Boolean plannedBasalInjectionsAdded = false;
    private Boolean exercisesAdded = false;
    private Boolean entriesAdded = false;
    private Boolean notificationsAdded = false;
    private Boolean observationsAdded = false;

    private Boolean permissionGranted = true;

    private Boolean documentCreated = false;

    private Button buttonGetPdf;
    private Button buttonClose;

    private DataViewModel viewModel;
    private User user;
    private List<Daytime> daytimeList;
    private List<Sport> sportList;
    private List<PlannedBasalInjection> plannedBasalInjectionList;
    private List<Exercise> exerciseList;
    private List<Entry> entryList;
    private List<Notification> notificationList;
    private List<Observation> observationList;

    private File myFile;
    private Document document;

    public ExportDialogFragment() {
        // Required empty public constructor
    }

    public static ExportDialogFragment newInstance() {
        ExportDialogFragment fragment = new ExportDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //get alert dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //set new View to the builder and create the Dialog
        Dialog dialog = builder.setView(new View(getActivity())).create();

        //get WindowManager.LayoutParams, copy attributes from Dialog to LayoutParams and override them with MATCH_PARENT
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //show the Dialog before setting new LayoutParams to the Dialog
        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings_dialog_export, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionGranted = false;
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }

        buttonGetPdf = view.findViewById(R.id.button_get_pdf);
        buttonClose = view.findViewById(R.id.button_close_export);

        viewModel = ViewModelProviders.of(this).get(DataViewModel.class);

        //start observing all lists that are needed for the export
        viewModel.getUsers().observe(ExportDialogFragment.this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable final List<User> users) {
//                initialize user
//                if user does not exist, create new user
                try {
                    user = users.get(0);
                    userAdded = true;
                    if (permissionGranted && userAdded && daytimesAdded && sportsAdded && plannedBasalInjectionsAdded && exercisesAdded && entriesAdded && notificationsAdded && observationsAdded) {
                        try {
                            createDocument();
                        } catch (FileNotFoundException | DocumentException e) {
                            System.out.println("EXCEPTION OCCURED WHILE CREATING PDF" + "\n" + e.toString());
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    user = new User();
                    viewModel.addUser(user);
                    Toast toast = Toast.makeText(getContext(), "Created new user", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

        viewModel.getDaytimes().observe(ExportDialogFragment.this, new Observer<List<Daytime>>() {
            @Override
            public void onChanged(@Nullable List<Daytime> daytimes) {
                daytimeList = daytimes;
                daytimesAdded = true;
                if (permissionGranted && userAdded && daytimesAdded && sportsAdded && plannedBasalInjectionsAdded && exercisesAdded && entriesAdded && notificationsAdded && observationsAdded) {
                    try {
                        createDocument();
                    } catch (FileNotFoundException | DocumentException e) {
                        System.out.println("EXCEPTION OCCURED WHILE CREATING PDF" + "\n" + e.toString());
                    }
                }
            }
        });

        viewModel.getSports().observe(ExportDialogFragment.this, new Observer<List<Sport>>() {
            @Override
            public void onChanged(@Nullable List<Sport> sports) {
                sportList = sports;
                sportsAdded = true;
                if (permissionGranted && userAdded && daytimesAdded && sportsAdded && plannedBasalInjectionsAdded && exercisesAdded && entriesAdded && notificationsAdded && observationsAdded) {
                    try {
                        createDocument();
                    } catch (FileNotFoundException | DocumentException e) {
                        System.out.println("EXCEPTION OCCURED WHILE CREATING PDF" + "\n" + e.toString());
                    }
                }
            }
        });

        viewModel.getPlannedBasalInjections().observe(ExportDialogFragment.this, new Observer<List<PlannedBasalInjection>>() {
            @Override
            public void onChanged(@Nullable List<PlannedBasalInjection> plannedBasalInjections) {
                plannedBasalInjectionList = plannedBasalInjections;
                plannedBasalInjectionsAdded = true;
                if (permissionGranted && userAdded && daytimesAdded && sportsAdded && plannedBasalInjectionsAdded && exercisesAdded && entriesAdded && notificationsAdded && observationsAdded) {
                    try {
                        createDocument();
                    } catch (FileNotFoundException | DocumentException e) {
                        System.out.println("EXCEPTION OCCURED WHILE CREATING PDF" + "\n" + e.toString());
                    }
                }
            }
        });

        viewModel.getExercises().observe(ExportDialogFragment.this, new Observer<List<Exercise>>() {
            @Override
            public void onChanged(@Nullable List<Exercise> exercises) {
                exerciseList = exercises;
                exercisesAdded = true;
                if (permissionGranted && userAdded && daytimesAdded && sportsAdded && plannedBasalInjectionsAdded && exercisesAdded && entriesAdded && notificationsAdded && observationsAdded) {
                    try {
                        createDocument();
                    } catch (FileNotFoundException | DocumentException e) {
                        System.out.println("EXCEPTION OCCURED WHILE CREATING PDF" + "\n" + e.toString());
                    }
                }
            }
        });

        viewModel.getEntries().observe(ExportDialogFragment.this, new Observer<List<Entry>>() {
            @Override
            public void onChanged(@Nullable List<Entry> entries) {
                entryList = entries;
                entriesAdded = true;
                if (permissionGranted && userAdded && daytimesAdded && sportsAdded && plannedBasalInjectionsAdded && exercisesAdded && entriesAdded && notificationsAdded && observationsAdded) {
                    try {
                        createDocument();
                    } catch (FileNotFoundException | DocumentException e) {
                        System.out.println("EXCEPTION OCCURED WHILE CREATING PDF" + "\n" + e.toString());
                    }
                }
            }
        });

        viewModel.getNotifications().observe(ExportDialogFragment.this, new Observer<List<Notification>>() {
            @Override
            public void onChanged(@Nullable List<Notification> notifications) {
                notificationList = notifications;
                notificationsAdded = true;
                if (permissionGranted && userAdded && daytimesAdded && sportsAdded && plannedBasalInjectionsAdded && exercisesAdded && entriesAdded && notificationsAdded && observationsAdded) {
                    try {
                        createDocument();
                    } catch (FileNotFoundException | DocumentException e) {
                        System.out.println("EXCEPTION OCCURED WHILE CREATING PDF" + "\n" + e.toString());
                    }
                }
            }
        });

        viewModel.getObservations().observe(ExportDialogFragment.this, new Observer<List<Observation>>() {
            @Override
            public void onChanged(@Nullable List<Observation> observations) {
                observationList = observations;
                observationsAdded = true;
                if (permissionGranted && userAdded && daytimesAdded && sportsAdded && plannedBasalInjectionsAdded && exercisesAdded && entriesAdded && notificationsAdded && observationsAdded) {
                    try {
                        createDocument();
                    } catch (FileNotFoundException | DocumentException e) {
                        System.out.println("EXCEPTION OCCURED WHILE CREATING PDF" + "\n" + e.toString());
                    }
                }
            }
        });

        //set clicklistener to button
        buttonGetPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (documentCreated) viewPdf();
                else {
                    Toast toast = Toast.makeText(getContext(), "Please wait for the document to be created", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnExportDialogFragmentInteractionListener) {
            mListener = (OnExportDialogFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnBasalinsulinDialogFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnExportDialogFragmentInteractionListener {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch(requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted = true;
                } else {
                    Toast toast = Toast.makeText(getContext(), "Please allow access to external storage to export pdf", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void createDocument() throws FileNotFoundException, DocumentException {
        File pdfFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "dmate");
        if (!pdfFolder.exists()) {
            pdfFolder.mkdir();
            Toast toast = Toast.makeText(getContext(), "PDF directory created", Toast.LENGTH_LONG);
            toast.show();
        }

        myFile = new File(pdfFolder +  ".pdf");

        OutputStream output = new FileOutputStream(myFile, false);

        document = new Document();

        PdfWriter.getInstance(document, output);
        document.open();

        //set header including user name
        if (user.getName() != null) {
            Paragraph p1 = new Paragraph(new Phrase("DMate export for " + user.getName(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 28f)));
            p1.setSpacingBefore(72f);
            document.add(p1);
        }
        else {
            Paragraph p1 = new Paragraph(new Phrase("DMate export " + user.getName(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 28f)));
            p1.setSpacingBefore(72f);
            document.add(p1);
        }

        //set date
        String date = Helper.formatMillisToDateString(System.currentTimeMillis());
        Paragraph p2 = new Paragraph(new Phrase(date, FontFactory.getFont(FontFactory.HELVETICA, 16f)));
        p2.setSpacingAfter(72f);
        document.add(p2);



        //create tableUserInfo
        PdfPTable tableUserInfo = new PdfPTable(new float[]{1, 1});
        tableUserInfo.setHeaderRows(1);
        tableUserInfo.setSpacingAfter(72f);

        //set user name
        tableUserInfo.addCell("User name");
        if (user.getName() != null) tableUserInfo.addCell(user.getName());
        else tableUserInfo.addCell("");

        //set bolus insulin information
        tableUserInfo.addCell("Bolus insulin name");
        if (user.getBolusName() != null) tableUserInfo.addCell(user.getBolusName());
        else tableUserInfo.addCell("");
        tableUserInfo.addCell("Bolus insulin duration of action");
        if (user.getBolusDuration() != null) tableUserInfo.addCell(user.getBolusDuration().toString());
        else tableUserInfo.addCell("");

        //set basal insulin information
        tableUserInfo.addCell("Basal insulin name");
        if (user.getBasalName() != null) tableUserInfo.addCell(user.getBasalName());
        else tableUserInfo.addCell("");
        tableUserInfo.addCell("Basal insulin duration of action");
        if (user.getBasalDuration() != null) tableUserInfo.addCell(user.getBasalDuration().toString());
        else tableUserInfo.addCell("");

        //set target area
        tableUserInfo.addCell("Lower end of target area");
        if (user.getTargetMin() != null) tableUserInfo.addCell(user.getTargetMin().toString());
        else tableUserInfo.addCell("");
        tableUserInfo.addCell("Upper end of target area");
        if (user.getTargetMax() != null) tableUserInfo.addCell(user.getTargetMax().toString());
        else tableUserInfo.addCell("");

        //set units
        tableUserInfo.addCell("Unit for carbohydrates");
        if (user.getUnitBu()) tableUserInfo.addCell("Bread units");
        else tableUserInfo.addCell("Carbohydrate units");
        tableUserInfo.addCell("Unit for blood sugar values");
        if (user.getUnitMgdl()) tableUserInfo.addCell("mg/dl");
        else tableUserInfo.addCell("mmol/l");

        //add user information table to document
        document.add(tableUserInfo);


        //create planned basal injections table
        PdfPTable tablePbi = new PdfPTable(new float[]{1,1});
        tablePbi.setHeaderRows(1);
        tablePbi.setSpacingAfter(72f);

        if (plannedBasalInjectionList.size() == 0) {
            tablePbi.addCell("No planned basal insulin injections defined");
            tablePbi.addCell("-");
            tablePbi.addCell("-");
            tablePbi.addCell("-");
        } else {
            int counter = 1;
            for (PlannedBasalInjection pbi : plannedBasalInjectionList) {
                PdfPCell cellDaytimeColored = new PdfPCell();
                BaseColor color = new BaseColor(getResources().getColor(R.color.colorAccent));
                cellDaytimeColored.setBackgroundColor(color);
                cellDaytimeColored.setPhrase(new Phrase("Planned basal insulin injection " + counter));
                tablePbi.addCell(cellDaytimeColored);
                cellDaytimeColored.setPhrase(new Phrase(""));
                tablePbi.addCell(cellDaytimeColored);
                tablePbi.addCell("Time of day");
                tablePbi.addCell(pbi.getTimeOfDay());
                tablePbi.addCell("Basal insulin injection");
                tablePbi.addCell(pbi.getBasal().toString());
                counter++;
            }
        }

        //add pbiTable to document
        document.add(tablePbi);


        //create table daytimes
        PdfPTable tableDaytimes = new PdfPTable(new float[]{1,1});
        tableDaytimes.setHeaderRows(1);
        tableDaytimes.setSpacingAfter(72f);

        if (daytimeList.size() == 0) {
            tableDaytimes.addCell("No daytimes defined");
            tableDaytimes.addCell("-");
            tableDaytimes.addCell("-");
            tableDaytimes.addCell("-");
        } else {
            int counter = 1;
            for (Daytime d : daytimeList) {
                PdfPCell cellDaytimeColored = new PdfPCell();
                BaseColor color = new BaseColor(getResources().getColor(R.color.colorAccent));
                cellDaytimeColored.setBackgroundColor(color);
                cellDaytimeColored.setPhrase(new Phrase("Daytime " + counter));
                tableDaytimes.addCell(cellDaytimeColored);
                cellDaytimeColored.setPhrase(new Phrase(""));
                tableDaytimes.addCell(cellDaytimeColored);
                tableDaytimes.addCell("ID");
                tableDaytimes.addCell(d.getdId().toString());
                tableDaytimes.addCell("Start of daytime");
                tableDaytimes.addCell(d.getDaytimeStart());
                tableDaytimes.addCell("End of daytime");
                tableDaytimes.addCell(d.getDaytimeEnd());
                tableDaytimes.addCell("Bread unit factor");
                tableDaytimes.addCell(d.getBuFactor().toString());
                tableDaytimes.addCell("Correction factor");
                tableDaytimes.addCell(d.getCorrectionFactor().toString());

                counter++;
            }
        }

        document.add(tableDaytimes);


        //create table sports
        PdfPTable tableSports = new PdfPTable(new float[]{1,1});
        tableSports.setHeaderRows(1);
        tableSports.setSpacingAfter(72f);

        if (sportList.size() == 0) {
            tableSports.addCell("No sportive activities defined");
            tableSports.addCell("-");
            tableSports.addCell("-");
            tableSports.addCell("-");
        } else {
            int counter = 1;
            for (Sport s : sportList) {
                PdfPCell cellSportColored = new PdfPCell();
                BaseColor color = new BaseColor(getResources().getColor(R.color.colorAccent));
                cellSportColored.setBackgroundColor(color);
                cellSportColored.setPhrase(new Phrase("Sportive activity " + counter));
                tableSports.addCell(cellSportColored);
                cellSportColored.setPhrase(new Phrase(""));
                tableSports.addCell(cellSportColored);
                tableSports.addCell("Name");
                tableSports.addCell(s.getSportName());
                tableSports.addCell("Effect per unit");
                tableSports.addCell(s.getSportEffectPerUnit().toString());
                counter++;
            }
        }

        document.add(tableSports);


        //create table notifications
        PdfPTable tableNotifications = new PdfPTable(new float[]{1,1});
        tableNotifications.setSpacingAfter(72f);

        if (notificationList.size() == 0) {
            tableNotifications.addCell("No notifications available");
            tableNotifications.addCell("-");
            tableNotifications.addCell("-");
            tableNotifications.addCell("-");
        } else {
            int counter = 1;
            for (Notification n : notificationList) {
                PdfPCell cellNotificationColored = new PdfPCell();
                BaseColor color = new BaseColor(getResources().getColor(R.color.colorAccent));
                cellNotificationColored.setBackgroundColor(color);
                cellNotificationColored.setPhrase(new Phrase("Notification " + counter));
                tableNotifications.addCell(cellNotificationColored);
                cellNotificationColored.setPhrase(new Phrase(""));
                tableNotifications.addCell(cellNotificationColored);
                if (n.getTimestamp() != null) {
                    tableNotifications.addCell("Date");
                    tableNotifications.addCell(Helper.formatMillisToDateString(n.getTimestamp().getTime()));
                    tableNotifications.addCell("Time");
                    tableNotifications.addCell(Helper.formatMillisToTimeString(n.getTimestamp().getTime()));
                }
                tableNotifications.addCell("Notification type");
                tableNotifications.addCell(n.getNotificationType().toString());
                tableNotifications.addCell("Message");
                tableNotifications.addCell(n.getMessage());
                counter++;
            }
        }
        document.add(tableNotifications);


        //create table observations
        PdfPTable tableObservations = new PdfPTable(new float[]{1,1});
        tableObservations.setSpacingAfter(72f);

        if (observationList.size() == 0) {
            tableObservations.addCell("No observations available");
            tableObservations.addCell("-");
            tableObservations.addCell("-");
            tableObservations.addCell("-");
        } else {
            int counter = 1;
            for (Observation o : observationList) {
                PdfPCell cellObservationColored = new PdfPCell();
                BaseColor color = new BaseColor(getResources().getColor(R.color.colorAccent));
                cellObservationColored.setBackgroundColor(color);
                cellObservationColored.setPhrase(new Phrase("Observation " + counter));
                tableObservations.addCell(cellObservationColored);
                cellObservationColored.setPhrase(new Phrase(""));
                tableObservations.addCell(cellObservationColored);
                tableObservations.addCell("ID");
                tableObservations.addCell(o.getOId().toString());
                tableObservations.addCell("ID of start entry");
                tableObservations.addCell(o.getEIdStart().toString());
                tableObservations.addCell("ID of end entry");
                if (o.getEIdEnd() != null) tableObservations.addCell(o.getEIdEnd().toString());
                else tableObservations.addCell("-");
                tableObservations.addCell("Divergence from start entry");
                if (o.getDivergenceFromStart() != null) tableObservations.addCell(o.getDivergenceFromStart().toString());
                else tableObservations.addCell("-");
                counter++;
            }
        }
        document.add(tableObservations);


        //create table entries
        PdfPTable tableEntries = new PdfPTable(new float[]{1,1});
        tableEntries.setSpacingAfter(72f);

        if (entryList.size() == 0) {
            tableEntries.addCell("No entries available");
            tableEntries.addCell("-");
            tableEntries.addCell("-");
            tableEntries.addCell("-");
        } else {
            int counter = 1;
            for (Entry e : entryList) {
                PdfPCell cellEntryColored = new PdfPCell();
                BaseColor color = new BaseColor(getResources().getColor(R.color.colorAccent));
                cellEntryColored.setBackgroundColor(color);
                cellEntryColored.setPhrase(new Phrase("Entry " + counter));
                tableEntries.addCell(cellEntryColored);
                cellEntryColored.setPhrase(new Phrase(""));
                tableEntries.addCell(cellEntryColored);
                tableEntries.addCell("ID");
                tableEntries.addCell(e.geteId().toString());
                tableEntries.addCell("Date");
                tableEntries.addCell(Helper.formatMillisToDateString(e.getTimestamp().getTime()));
                tableEntries.addCell("Time");
                tableEntries.addCell(Helper.formatMillisToTimeString(e.getTimestamp().getTime()));
                tableEntries.addCell("Blood sugar");
                if (e.getBloodSugar() != null) tableEntries.addCell(e.getBloodSugar().toString());
                else tableEntries.addCell("-");
                tableEntries.addCell("Bread units / Carb units");
                if (e.getBreadUnit() != null) tableEntries.addCell(e.getBreadUnit().toString());
                else tableEntries.addCell("-");
                tableEntries.addCell("Bolus insulin injection");
                if (e.getBolus() != null) tableEntries.addCell(e.getBolus().toString());
                else tableEntries.addCell("-");
                tableEntries.addCell("Basal insulin injection");
                if (e.getBasal() != null) tableEntries.addCell(e.getBasal().toString());
                else tableEntries.addCell("-");
                tableEntries.addCell("Note");
                if (e.getNote() != null) tableEntries.addCell(e.getNote());
                else tableEntries.addCell("-");
                tableEntries.addCell("Diseased");
                if (e.getDiseased() != null) tableEntries.addCell(e.getDiseased().toString());
                else tableEntries.addCell("-");
                tableEntries.addCell("Daytime ID");
                if (e.getdIdF() != null) tableEntries.addCell(e.getdIdF().toString());
                else tableEntries.addCell("-");

                int counterEx = 1;
                for (Exercise ex : exerciseList) {
                    if (e.geteId().equals(ex.geteIdF())) {
                        for (Sport s : sportList) {
                            if (s.getsId().equals(ex.getsIdF())) {
                                tableEntries.addCell("Exercise " + counterEx);
                                tableEntries.addCell("-");
                                tableEntries.addCell("Exercise ID");
                                if (ex.getExId() != null) tableEntries.addCell(ex.getExId().toString());
                                else tableEntries.addCell("-");
                                tableEntries.addCell("Kind of sport");
                                tableEntries.addCell(s.getSportName());
                                tableEntries.addCell("Effect per unit");
                                tableEntries.addCell(s.getSportEffectPerUnit().toString());
                                tableEntries.addCell("Units of sport");
                                tableEntries.addCell(ex.getExerciseUnits().toString());
                            }
                        }
                    }
                    counterEx++;
                }
                counter++;
            }
        }

        document.add(tableEntries);


        //close document
        document.close();

        documentCreated = true;
    }

    private void viewPdf() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".fileprovider", myFile);
        intent.setDataAndType(uri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        getContext().startActivity(intent);
    }
}
