package app.chat.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import app.chat.fragments.ChatsFragment;
import app.chat.fragments.ContactsFragment;
import app.chat.fragments.GroupFragment;

public class TabsAccessorAdapters extends FragmentPagerAdapter {

    public TabsAccessorAdapters(FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0:
                ChatsFragment chatsFragment= new ChatsFragment();
                return  chatsFragment;

            case 1:
                GroupFragment groupFragment= new GroupFragment();
                return  groupFragment;

            case 2:
                ContactsFragment contactsFragment= new ContactsFragment();
                return  contactsFragment;

            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int i) {
        switch (i){
            case 0:
                return "Chats";

            case 1:
                return  "Groups";

            case 2:
                return "Contacts";

            default:
                return null;
        }
    }
}
