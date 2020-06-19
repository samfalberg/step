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
   
    ArrayList<Event> mandatoryEvents = new ArrayList<Event>();
    ArrayList<Event> optionalEvents = new ArrayList<Event>();
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

        //Add to optional event list if not too long and attendees are only optionals
        if (hasOnlyOptionals(event, request)) {
            if (event.getWhen().duration() + request.getDuration() < TimeRange.WHOLE_DAY.duration()) {   
                optionalEvents.add(event);
            } else {
                continue;
            }
        } else {   //Else add to mandatory event list
            mandatoryEvents.add(event);
        }

        totalEventTime += event.getWhen().duration();
    }

    //Return empty list if meeting is longer than entire day
    if (request.getDuration() > TimeRange.WHOLE_DAY.duration()) {
        return new ArrayList<TimeRange>();
    }

    //If there are no events, or we're scheduling for people not attending, the whole day is free
    if (events.isEmpty() || !isAttending) {
        return Arrays.asList(TimeRange.WHOLE_DAY);
    } 

    //The only events have only optional attendees with no gaps in their schedules
    if (mandatoryEvents.isEmpty() && optionalEvents.isEmpty()) {
        return new ArrayList<TimeRange>();
    }
    
    //Only optional events, find gaps in schedule
    if (mandatoryEvents.isEmpty()) {
        return freeTimes(optionalEvents, request);
    } else {   //Includes mandatory events, find gaps in schedule and compare with optional gaps
        return freeTimes(mandatoryEvents, request);
    }
    
  }

  /** 
   * Finds valid time ranges for attendees, returns them as a collection
   */
  public Collection<TimeRange> freeTimes(ArrayList<Event> events, MeetingRequest request) {
    Collection<TimeRange> freeTimes = new ArrayList<TimeRange>();

    //List of events ordered by start time
    ArrayList<Event> startTimeEvents = new ArrayList<Event>(events);
    Collections.sort(startTimeEvents, Event.ORDER_BY_START);

    //List of events ordered by end time
    ArrayList<Event> endTimeEvents = new ArrayList<Event>(events);
    Collections.sort(endTimeEvents, Event.ORDER_BY_END);

    //There's a valid event, so make time range from start til first event
    freeTimes.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, startTimeEvents.get(0).getWhen().start(), false));

    //If there's more than one event, and they don't overlap, get the time between them
    if (startTimeEvents.size() > 1) {
        
        for (int i = 0; i < startTimeEvents.size() - 1; i++) {
            
            if (!startTimeEvents.get(i).getWhen().overlaps(startTimeEvents.get(i+1).getWhen())) {
                
                int currentEventEnd = startTimeEvents.get(i).getWhen().end();
                int nextEventStart = startTimeEvents.get(i+1).getWhen().start();
                
                freeTimes.add(TimeRange.fromStartEnd(currentEventEnd, nextEventStart, false));
            }
        }
    }

    //Make time range from end of last events til end of day
    freeTimes.add(TimeRange.fromStartEnd(endTimeEvents.get(endTimeEvents.size() - 1).getWhen().end(), TimeRange.END_OF_DAY, true));

    //Remove all time ranges that are less than required meeting duration, return resulting list
    return freeTimes
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
