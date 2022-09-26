package com.example.mangareader.Common;

import com.example.mangareader.Model.Chapter;
import com.example.mangareader.Model.Comic;
import com.example.mangareader.Model.User;

import java.util.ArrayList;
import java.util.List;

public class Common {
    public static List<Comic> comicList = new ArrayList<>();
    public static Comic comicSelected;
    public static List<Chapter> chapterList;
    public static Chapter chapterSelected;
    public static int chapterIndex = -1;
    public static boolean Login = false;
    public static User currentUser;
    public static String ChipCategoryClicked = "";

    public static String formatString(String name) {
        StringBuilder s = new StringBuilder(name.length() > 15 ? name.substring(0, 15) + "..." : name);
        return s.toString();
    }

    public static String[] categories = {
            "Action",
            "Adventure",
            "Comedy",
            "Romance",
            "Drama",
            "Fantasy",
            "Shounen",
            "Shoujo",
            "School life",
            "Slice of life",
            "Gender bender",
            "Harem",
            "Historical",
            "Horror",
            "Supernatural",
            "Superhero",
            "Tragedy",
            "Mature",
            "Mecha",
            "Medical",
            "Mystery",
            "One shot",
            "Psychological",
            "Sci fi",
            "Sports",
            "Cooking",
            //"Newest",
            //"Ongoing",
            //"Top Read",
            //"Webtoons",
            //"Yaoi",
            //"Yuri",
            //"Adult",
            //"Seinen",
            //"Shoujo a",
            //"Shounen ai",
            //"Smut",
            //"Jose",
            //"Latest",
            //"Manhua",
            //"Manhwa",
            //"Drop",
            //"Ecchi",
            //"Completed",
            //"Doujinshi",
            "Material arts"
    };
}
