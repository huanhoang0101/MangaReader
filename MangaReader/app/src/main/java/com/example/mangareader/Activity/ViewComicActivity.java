package com.example.mangareader.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mangareader.Adapter.MyViewPagerAdapter;
import com.example.mangareader.Common.Common;
import com.example.mangareader.Model.Chapter;
import com.example.mangareader.R;
import com.wajahatkarim3.easyflipviewpager.BookFlipPageTransformer;

public class ViewComicActivity extends AppCompatActivity {

    ViewPager viewPager;
    TextView txtChapName;
    View back, next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_comic);

        AnhXa();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Common.chapterIndex == 0)
                    Toast.makeText(ViewComicActivity.this, getString(R.string.reading_first_chapter), Toast.LENGTH_SHORT).show();
                else
                {
                    Common.chapterIndex--;
                    fetchLinks(Common.chapterList.get(Common.chapterIndex));
                    txtChapName.setText(Common.chapterList.get(Common.chapterIndex).getName());
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Common.chapterIndex == Common.chapterList.size() - 1)
                    Toast.makeText(ViewComicActivity.this, getString(R.string.reading_last_chapter), Toast.LENGTH_SHORT).show();
                else
                {
                    Common.chapterIndex++;
                    fetchLinks(Common.chapterList.get(Common.chapterIndex));
                    txtChapName.setText(Common.chapterList.get(Common.chapterIndex).getName());
                }
            }
        });
        fetchLinks(Common.chapterSelected);
    }

    private void AnhXa() {
        viewPager = findViewById(R.id.view_pager);
        txtChapName = findViewById(R.id.txt_chapName);
        back = findViewById(R.id.chapter_back);
        next = findViewById(R.id.chapter_next);
    }

    private void fetchLinks(Chapter chapter) {
        if(chapter.getLinks() != null)
        {
            if(chapter.getLinks().size() > 0)
            {
                MyViewPagerAdapter adapter = new MyViewPagerAdapter(getBaseContext(), chapter.getLinks());
                viewPager.setAdapter(adapter);

                txtChapName.setText(Common.formatString(Common.chapterSelected.getName()));

                //Animation
                BookFlipPageTransformer bookFlipPageTransformer = new BookFlipPageTransformer();
                bookFlipPageTransformer.setScaleAmountPercent(10f);
                viewPager.setPageTransformer(true,bookFlipPageTransformer);
            }
            else
            {
                Toast.makeText(ViewComicActivity.this, getString(R.string.no_image), Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(ViewComicActivity.this, getString(R.string.loading_chapter), Toast.LENGTH_SHORT).show();
        }
    }
}