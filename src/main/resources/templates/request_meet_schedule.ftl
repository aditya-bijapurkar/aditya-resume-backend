<html>
  <body>
    <h3>Hello, A new meeting request has been initiated</h3>
    <p>The following people would join the meet: ${required_users}</p>
    <p>Email ids to connect with: ${email_ids}</p>
    <p>Agenda of the meeting: ${description}</p>
    <p>Schedule requested on ${schedule_date}, ${schedule_time} IST</p>
    <p>
      <a href="https://adityabijapurkar.in/schedule/meet/respond?meetingId=${meeting_id}&response=scheduled">
        Click here to Accept
      </a>
    </p>
    <p>
      <a href="https://adityabijapurkar.in/schedule/meet/respond?meetingId=${meeting_id}&response=declined">
        Click here to Reject
      </a>
    </p>
    <p>Regards,<br/>Aditya Bijapurkar’s System</p>
  </body>
</html>