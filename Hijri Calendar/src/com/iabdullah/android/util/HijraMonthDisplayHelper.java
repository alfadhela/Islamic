package com.iabdullah.android.util;

import org.tmrah.islamic.hijri.HijraCalendar;

public class HijraMonthDisplayHelper {

    // display pref
    private final int mWeekStartDay;

    // holds current month, year, helps compute display
    private HijraCalendar mCalendar;

    // cached computed stuff that helps with display
    private int mNumDaysInMonth;
    private int mNumDaysInPrevMonth;
    private int mOffset;


    /**
     * @param year The year.
     * @param month The month.
     * @param weekStartDay What day of the week the week should start.
     */
    public HijraMonthDisplayHelper(int year, int month, int weekStartDay) {

        if (weekStartDay < HijraCalendar.SUNDAY || weekStartDay > HijraCalendar.SATURDAY) {
            throw new IllegalArgumentException();
        }
        mWeekStartDay = weekStartDay;

        mCalendar = HijraCalendar.getInstance();
        mCalendar.set(HijraCalendar.YEAR, year);
        mCalendar.set(HijraCalendar.MONTH, month);
        mCalendar.set(HijraCalendar.DAY_OF_MONTH, 1);
        mCalendar.set(HijraCalendar.HOUR_OF_DAY, 0);
        mCalendar.set(HijraCalendar.MINUTE, 0);
        mCalendar.set(HijraCalendar.SECOND, 0);
        mCalendar.getTimeInMillis();

        recalculate();
    }


    public HijraMonthDisplayHelper(int year, int month) {
        this(year, month, HijraCalendar.SUNDAY);
    }


    public int getYear() {
        return mCalendar.get(HijraCalendar.YEAR);
    }

    public int getMonth() {
        return mCalendar.get(HijraCalendar.MONTH);
    }


    public int getWeekStartDay() {
        return mWeekStartDay;
    }

    /**
     * @return The first day of the month using a constants such as
     *   {@link java.util.Calendar#SUNDAY}.
     */
    public int getFirstDayOfMonth() {
        return mCalendar.get(HijraCalendar.DAY_OF_WEEK);
    }

    /**
     * @return The number of days in the month.
     */
    public int getNumberOfDaysInMonth() {
        return mNumDaysInMonth;
    }


    /**
     * @return The offset from displaying everything starting on the very first
     *   box.  For example, if the calendar is set to display the first day of
     *   the week as Sunday, and the month starts on a Wednesday, the offset is 3.
     */
    public int getOffset() {
        return mOffset;
    }


    /**
     * @param row Which row (0-5).
     * @return the digits of the month to display in one
     * of the 6 rows of a calendar month display.
     */
    public int[] getDigitsForRow(int row) {
        if (row < 0 || row > 5) {
            throw new IllegalArgumentException("row " + row
                    + " out of range (0-5)");
        }

        int [] result = new int[7];
        for (int column = 0; column < 7; column++) {
            result[column] = getDayAt(row, column);
        }

        return result;
    }

    /**
     * @param row The row, 0-5, starting from the top.
     * @param column The column, 0-6, starting from the left.
     * @return The day at a particular row, column
     */
    public int getDayAt(int row, int column) {

        if (row == 0 && column < mOffset) {
            return mNumDaysInPrevMonth + column - mOffset + 1;
        }

        int day = 7 * row + column - mOffset + 1;

        return (day > mNumDaysInMonth) ?
                day - mNumDaysInMonth : day;
    }

    /**
     * @return Which row day is in.
     */
    public int getRowOf(int day) {
        return (day + mOffset - 1) / 7;
    }

    /**
     * @return Which column day is in.
     */
    public int getColumnOf(int day) {
        return (day + mOffset - 1) % 7;
    }

    /**
     * Decrement the month.
     */
    public void previousMonth() {
        mCalendar.add(HijraCalendar.MONTH, -1);
        recalculate();
    }

    /**
     * Increment the month.
     */
    public void nextMonth() {
        mCalendar.add(HijraCalendar.MONTH, 1);
        recalculate();
    }

    /**
     * @return Whether the row and column fall within the month.
     */
    public boolean isWithinCurrentMonth(int row, int column) {

        if (row < 0 || column < 0 || row > 5 || column > 6) {
            return false;
        }

        if (row == 0 && column < mOffset) {
            return false;
        }

        int day = 7 * row + column - mOffset;
        if (day > mNumDaysInMonth) {
            return false;
        }
        return true;
    }


    // helper method that recalculates cached values based on current month / year
    private void recalculate() {

        mNumDaysInMonth = mCalendar.getActualMaximum(HijraCalendar.DAY_OF_MONTH);
        System.out.println("mNumDaysInMonth=" + mNumDaysInMonth);

        mCalendar.add(HijraCalendar.MONTH, -1);
        mNumDaysInPrevMonth = mCalendar.getActualMaximum(HijraCalendar.DAY_OF_MONTH);
        mCalendar.add(HijraCalendar.MONTH, 1);

        int firstDayOfMonth = getFirstDayOfMonth();
        int offset = firstDayOfMonth - mWeekStartDay;
        if (offset < 0) {
            offset += 7;
        }
        mOffset = offset;
    }
}