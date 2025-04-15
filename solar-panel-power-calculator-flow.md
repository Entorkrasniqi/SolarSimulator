flowchart TD
    Start[Start Application] --> InitConfig[Initialize Configuration]
    InitConfig --> UserInput[Get Solar Panel Settings from User]
    UserInput --> ValidateInput{Validate User Input}
    ValidateInput -->|Invalid| UserInput
    ValidateInput -->|Valid| InitAPI[Initialize API Connection]
    
    InitAPI --> EventLoop[Enter Event Loop]
    
    subgraph EventLoop [Event Loop]
        CheckUpdate[Check for Update Interval] --> FetchRequired{Fetch Required?}
        FetchRequired -->|No| Wait[Wait for Next Interval]
        Wait --> CheckUpdate
        FetchRequired -->|Yes| FetchWeather[Fetch Weather Data from API]
        FetchWeather --> FetchSolar[Fetch Solar Radiation Data from API]
        FetchSolar --> ValidateAPI{Validate API Data}
        ValidateAPI -->|Invalid| HandleError[Handle API Error]
        HandleError --> Wait
        ValidateAPI -->|Valid| CalculatePower[Calculate Power Output]
        
        CalculatePower --> UpdateDisplay[Update Power Output Display]
        UpdateDisplay --> LogData[Log Data to History]
        LogData --> Wait
    end
    
    EventLoop --> UserExit{User Exit?}
    UserExit -->|No| EventLoop
    UserExit -->|Yes| CleanupResources[Cleanup Resources]
    CleanupResources --> SaveSettings[Save User Settings]
    SaveSettings --> End[End Application]
    
    subgraph CalculatePower [Power Calculation Process]
        PrepData[Prepare Input Data] --> ApplyEfficiency[Apply Panel Efficiency]
        ApplyEfficiency --> ApplyArea[Apply Panel Area]
        ApplyArea --> ApplyAngle[Apply Tilt Angle]
        ApplyAngle --> ApplyOrientation[Apply Panel Orientation]
        ApplyOrientation --> AdjustTemp[Adjust for Temperature]
        AdjustTemp --> AdjustWeather[Adjust for Weather Conditions]
        AdjustWeather --> CalculateOutput[Calculate Final Output]
    end
