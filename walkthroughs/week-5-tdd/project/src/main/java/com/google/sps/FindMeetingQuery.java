// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<TimeRange> findMeeting = new ArrayList<TimeRange>();
    
    ArrayList<Event> eventsList = new ArrayList<Event>();
    boolean isAttending = false;
    int totalEventTime = 0;

    for (Event event : events) {
        //Check if any mandatory attendees we're scheduling is attending any of the events
        for (String attendent : request.getAttendees()) {
            if (event.getAttendees().contains(attendent)) {
                isAttending = true;
                break;
            }
        }

        //Check if any optional attendees we're scheduling is attending any of the events
        for (String optionalAttendent : request.getOptionalAttendees()) {
            if (event.getAttendees().contains(optionalAttendent)) {
                isAttending = true;
                break;
            }
        }

        //Don't add event if it's too long to allow a meeting and only has optionals
        if (event.getWhen().duration() + request.getDuration() > TimeRange.WHOLE_DAY.duration() && hasOnlyOptionals(event, request)) {
            continue;
        }

        eventsList.add(event);
        totalEventTime += event.getWhen().duration();
    }

    //Return empty list if meeting is longer than entire day
    if (request.getDuration() > TimeRange.WHOLE_DAY.duration()) {
        return findMeeting;
    }

    //If there are no events, or we're scheduling for people not attending, the whole day is free
    if (events.isEmpty() || !isAttending) {
        findMeeting.add(TimeRange.WHOLE_DAY);
        return findMeeting;
    } 

    //The only events have optional attendees with no gaps in their schedules
    if (eventsList.isEmpty()) {
        return findMeeting;
    }
    
    //List of events ordered by start time
    ArrayList<Event> startTimeEventsList = new ArrayList<Event>(eventsList);
    Collections.sort(startTimeEventsList, Event.ORDER_BY_START);

    //List of events ordered by end time
    ArrayList<Event> endTimeEventsList = new ArrayList<Event>(eventsList);
    Collections.sort(endTimeEventsList, Event.ORDER_BY_END);

    //There's a valid event, so make time range from start til first event
    findMeeting.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, startTimeEventsList.get(0).getWhen().start(), false));

    //If there's more than one event, and they don't overlap, get the time between them
    if (startTimeEventsList.size() > 1) {
        
        for (int i = 0; i < startTimeEventsList.size() - 1; i++) {
            
            if (!startTimeEventsList.get(i).getWhen().overlaps(startTimeEventsList.get(i+1).getWhen())) {
                
                int currentEventEnd = startTimeEventsList.get(i).getWhen().end();
                int nextEventStart = startTimeEventsList.get(i+1).getWhen().start();
                
                findMeeting.add(TimeRange.fromStartEnd(currentEventEnd, nextEventStart, false));

                //Optional-only event has made it so there isn't enough time for meeting
                //Previous time range will get deleted in lines 117-120, add correct time range here
                if (hasOnlyOptionals(startTimeEventsList.get(i), request) && 
                    (totalEventTime + request.getDuration()) > TimeRange.WHOLE_DAY.duration()) {
                    //If it's the first element of the list, time range must start at beginning of day
                    int previousEventEnd = TimeRange.START_OF_DAY;
                    if (i > 0) {
                        previousEventEnd = startTimeEventsList.get(i - 1).getWhen().end();
                    }
                    findMeeting.add(TimeRange.fromStartEnd(previousEventEnd, nextEventStart, false));
                }
            }
        }
    }

    //Make time range from end of last events til end of day
    findMeeting.add(TimeRange.fromStartEnd(endTimeEventsList.get(eventsList.size() - 1).getWhen().end(), TimeRange.END_OF_DAY, true));

    //Remove all time ranges that are less than required meeting duration, return resulting list
    return findMeeting
            .stream()
            .filter(meeting -> meeting.duration() >= request.getDuration())
            .collect(Collectors.toList());
  }

  /**
   * Checks if an event is attended only by optionals
   */
  public boolean hasOnlyOptionals(Event event, MeetingRequest request) {
    Iterator<String> iterator = request.getAttendees().iterator();

    while (iterator.hasNext()) {
        //Event has mandatory attendees
        if (event.getAttendees().contains(iterator.next())) {
            return false;
        }
    }

    //Event has optional attendees
    if (event.getAttendees().size() > 0) {
        return true;
    }

    return false;
  }
}
